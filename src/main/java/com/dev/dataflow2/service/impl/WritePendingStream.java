package com.dev.dataflow2.service.impl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;

import javax.annotation.concurrent.GuardedBy;

import org.json.JSONArray;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.bigquery.storage.v1.AppendRowsResponse;
import com.google.cloud.bigquery.storage.v1.BatchCommitWriteStreamsRequest;
import com.google.cloud.bigquery.storage.v1.BatchCommitWriteStreamsResponse;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.CreateWriteStreamRequest;
import com.google.cloud.bigquery.storage.v1.Exceptions;
import com.google.cloud.bigquery.storage.v1.Exceptions.StorageException;
import com.google.cloud.bigquery.storage.v1.FinalizeWriteStreamResponse;
import com.google.cloud.bigquery.storage.v1.JsonStreamWriter;
import com.google.cloud.bigquery.storage.v1.StorageError;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.cloud.bigquery.storage.v1.WriteStream;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Descriptors.DescriptorValidationException;

public class WritePendingStream {

	private static final long BATCH_SIZE = 1000;

	public static void writePendingStream(BigQueryWriteClient client, String projectId, String datasetName,
			String tableName, JSONArray jsonArray)
			throws DescriptorValidationException, InterruptedException, IOException {
		TableName parentTable = TableName.of(projectId, datasetName, tableName);

		DataWriter writer = new DataWriter();
		writer.initialize(parentTable, client);

		try {
			long batches = (long) jsonArray.length() / BATCH_SIZE;
			for (int offset = 0; offset <= batches; offset++) {
				JSONArray currentBatch = new JSONArray();
				for (long currentIndex = (offset * BATCH_SIZE); currentIndex < Math.min(((offset + 1) * BATCH_SIZE),
						jsonArray.length()); currentIndex++) {
					currentBatch.put(jsonArray.getJSONObject((int) currentIndex));
				}
				writer.append(currentBatch, offset);
				offset += currentBatch.length();
			}
		} catch (ExecutionException e) {
			// If the wrapped exception is a StatusRuntimeException, check the state of the
			// operation.
			// If the state is INTERNAL, CANCELLED, or ABORTED, you can retry. For more
			// information, see:
			// https://grpc.github.io/grpc-java/javadoc/io/grpc/StatusRuntimeException.html
			System.out.println("Failed to append records. \n" + e);
		}

		// Final cleanup for the stream.
		writer.cleanup(client);
		System.out.println("Appended records successfully.");

		// Once all streams are done, if all writes were successful, commit all of them
		// in one request.
		// This example only has the one stream. If any streams failed, their workload
		// may be
		// retried on a new stream, and then only the successful stream should be
		// included in the
		// commit.
		BatchCommitWriteStreamsRequest commitRequest = BatchCommitWriteStreamsRequest.newBuilder()
				.setParent(parentTable.toString()).addWriteStreams(writer.getStreamName()).build();
		BatchCommitWriteStreamsResponse commitResponse = client.batchCommitWriteStreams(commitRequest);
		// If the response does not have a commit time, it means the commit operation
		// failed.
		if (commitResponse.hasCommitTime() == false) {
			for (StorageError err : commitResponse.getStreamErrorsList()) {
				System.out.println(err.getErrorMessage());
			}
			throw new RuntimeException("Error committing the streams");
		}
		System.out.println("Appended and committed records successfully.");
	}

	// A simple wrapper object showing how the stateful stream writer should be
	// used.
	private static class DataWriter {

		private JsonStreamWriter streamWriter;
		// Track the number of in-flight requests to wait for all responses before
		// shutting down.
		private final Phaser inflightRequestCount = new Phaser(1);

		private final Object lock = new Object();

		@GuardedBy("lock")
		private RuntimeException error = null;

		void initialize(TableName parentTable, BigQueryWriteClient client)
				throws IOException, DescriptorValidationException, InterruptedException {
			// Initialize a write stream for the specified table.
			// For more information on WriteStream.Type, see:
			// https://googleapis.dev/java/google-cloud-bigquerystorage/latest/com/google/cloud/bigquery/storage/v1/WriteStream.Type.html
			WriteStream stream = WriteStream.newBuilder().setType(WriteStream.Type.PENDING).build();

			CreateWriteStreamRequest createWriteStreamRequest = CreateWriteStreamRequest.newBuilder()
					.setParent(parentTable.toString()).setWriteStream(stream).build();
			WriteStream writeStream = client.createWriteStream(createWriteStreamRequest);

			// Use the JSON stream writer to send records in JSON format.
			// For more information about JsonStreamWriter, see:
			// https://googleapis.dev/java/google-cloud-bigquerystorage/latest/com/google/cloud/bigquery/storage/v1beta2/JsonStreamWriter.html
			streamWriter = JsonStreamWriter.newBuilder(writeStream.getName(), writeStream.getTableSchema(), client)
					.build();
		}

		public void append(JSONArray data, long offset)
				throws DescriptorValidationException, IOException, ExecutionException {
			synchronized (this.lock) {
				// If earlier appends have failed, we need to reset before continuing.
				if (this.error != null) {
					throw this.error;
				}
			}
			// Append asynchronously for increased throughput.
			ApiFuture<AppendRowsResponse> future = streamWriter.append(data, offset);
			ApiFutures.addCallback(future, new AppendCompleteCallback(this), MoreExecutors.directExecutor());
			// Increase the count of in-flight requests.
			inflightRequestCount.register();
		}

		public void cleanup(BigQueryWriteClient client) {
			// Wait for all in-flight requests to complete.
			inflightRequestCount.arriveAndAwaitAdvance();

			// Close the connection to the server.
			streamWriter.close();

			// Verify that no error occurred in the stream.
			synchronized (this.lock) {
				if (this.error != null) {
					throw this.error;
				}
			}

			// Finalize the stream.
			FinalizeWriteStreamResponse finalizeResponse = client.finalizeWriteStream(streamWriter.getStreamName());
			System.out.println("Rows written: " + finalizeResponse.getRowCount());
		}

		public String getStreamName() {
			return streamWriter.getStreamName();
		}

		static class AppendCompleteCallback implements ApiFutureCallback<AppendRowsResponse> {

			private final DataWriter parent;

			public AppendCompleteCallback(DataWriter parent) {
				this.parent = parent;
			}

			public void onSuccess(AppendRowsResponse response) {
				System.out.format("Append %d success\n", response.getAppendResult().getOffset().getValue());
				done();
			}

			public void onFailure(Throwable throwable) {
				synchronized (this.parent.lock) {
					if (this.parent.error == null) {
						StorageException storageException = Exceptions.toStorageException(throwable);
						this.parent.error = (storageException != null) ? storageException
								: new RuntimeException(throwable);
					}
				}
				System.out.format("Error: %s\n", throwable.toString());
				done();
			}

			private void done() {
				// Reduce the count of in-flight requests.
				this.parent.inflightRequestCount.arriveAndDeregister();
			}
		}
	}
}
