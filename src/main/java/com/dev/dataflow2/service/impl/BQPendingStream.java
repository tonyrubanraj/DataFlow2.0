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

public class BQPendingStream {

	private DataWriter writer;
	private TableName parentTable;

	public void createStream(BigQueryWriteClient client, String projectId, String datasetName, String tableName) {
		try {
			parentTable = TableName.of(projectId, datasetName, tableName);

			writer = new DataWriter();
			writer.initialize(parentTable, client);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DescriptorValidationException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void writeStream(JSONArray jsonArray, int offset) {
		try {
			writer.append(jsonArray, offset);
			offset += jsonArray.length();
		} catch (ExecutionException e) {
			System.out.println("Failed to append records. \n" + e);
		} catch (DescriptorValidationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeStream(BigQueryWriteClient client) {
		writer.cleanup(client);
		System.out.println("Appended records successfully.");
		BatchCommitWriteStreamsRequest commitRequest = BatchCommitWriteStreamsRequest.newBuilder()
				.setParent(parentTable.toString()).addWriteStreams(writer.getStreamName()).build();
		BatchCommitWriteStreamsResponse commitResponse = client.batchCommitWriteStreams(commitRequest);
		if (commitResponse.hasCommitTime() == false) {
			for (StorageError err : commitResponse.getStreamErrorsList()) {
				System.out.println(err.getErrorMessage());
			}
			throw new RuntimeException("Error committing the streams");
		}
		System.out.println("Appended and committed records successfully.");
	}

	private static class DataWriter {

		private JsonStreamWriter streamWriter;
		private final Phaser inflightRequestCount = new Phaser(1);

		private final Object lock = new Object();

		@GuardedBy("lock")
		private RuntimeException error = null;

		void initialize(TableName parentTable, BigQueryWriteClient client)
				throws IOException, DescriptorValidationException, InterruptedException {
			WriteStream stream = WriteStream.newBuilder().setType(WriteStream.Type.PENDING).build();

			CreateWriteStreamRequest createWriteStreamRequest = CreateWriteStreamRequest.newBuilder()
					.setParent(parentTable.toString()).setWriteStream(stream).build();
			WriteStream writeStream = client.createWriteStream(createWriteStreamRequest);

			streamWriter = JsonStreamWriter.newBuilder(writeStream.getName(), writeStream.getTableSchema(), client)
					.build();
		}

		public void append(JSONArray data, long offset)
				throws DescriptorValidationException, IOException, ExecutionException {
			synchronized (this.lock) {
				if (this.error != null) {
					throw this.error;
				}
			}
			ApiFuture<AppendRowsResponse> future = streamWriter.append(data, offset);
			ApiFutures.addCallback(future, new AppendCompleteCallback(this), MoreExecutors.directExecutor());
			inflightRequestCount.register();
		}

		public void cleanup(BigQueryWriteClient client) {
			inflightRequestCount.arriveAndAwaitAdvance();

			streamWriter.close();

			synchronized (this.lock) {
				if (this.error != null) {
					throw this.error;
				}
			}

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
				this.parent.inflightRequestCount.arriveAndDeregister();
			}
		}
	}
}
