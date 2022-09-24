package com.dev.dataflow2.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dev.dataflow2.dto.DBConnectionsDto;
import com.dev.dataflow2.pojo.BigQueryParameters;
import com.dev.dataflow2.service.DatabaseService;
import com.dev.dataflow2.utils.DatabaseUtils;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQuery.TableListOption;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.Dataset;
import com.google.cloud.bigquery.DatasetId;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.Table;
import com.google.cloud.bigquery.TableResult;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteClient;
import com.google.cloud.bigquery.storage.v1.BigQueryWriteSettings;
import com.google.protobuf.Descriptors.DescriptorValidationException;

public class BigQueryService extends DatabaseService {

	private GoogleCredentials credentials;
	private String projectId;
	private BigQuery bigQueryClient;

	public GoogleCredentials getCredentials() {
		return credentials;
	}

	@Override
	public boolean connect(DBConnectionsDto dbConnection) {
		try {
			BigQueryParameters bigQueryParameters = (BigQueryParameters) DatabaseUtils
					.getConnectionParamsMap(dbConnection.getDbType(), dbConnection.getConnectionParameters());
			this.credentials = ServiceAccountCredentials.fromPkcs8(bigQueryParameters.getClient_id(),
					bigQueryParameters.getClient_email(), bigQueryParameters.getPrivate_key(),
					bigQueryParameters.getPrivate_key_id(), null);
			this.projectId = bigQueryParameters.getProject_id();
			this.bigQueryClient = BigQueryOptions.newBuilder().setCredentials(this.credentials)
					.setProjectId(bigQueryParameters.getProject_id()).build().getService();
			return bigQueryClient != null ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void close() {
	}

	@Override
	public List<String> getSchemas() {
		List<String> schemas = new ArrayList<String>();
		for (Dataset dataset : this.bigQueryClient.listDatasets().iterateAll()) {
			schemas.add(dataset.getDatasetId().getDataset());
		}
		return schemas;
	}

	@Override
	public List<String> getTables() {
		List<String> tables = new ArrayList<String>();
		DatasetId datasetId = DatasetId.of(this.projectId, this.schema);
		Page<Table> tableList = this.bigQueryClient.listTables(datasetId, TableListOption.pageSize(100));
		tableList.iterateAll().forEach(table -> tables.add(table.getTableId().getTable()));
		getRecordsAsJson("target");
		return tables;
	}

	@Override
	public JSONArray getRecordsAsJson(String table) {
		JSONArray jsonArray = new JSONArray();
		String query = String.format("SELECT TO_JSON_STRING(t) FROM %s.%s AS t", this.schema, table);
		try {
			QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
			TableResult result = this.bigQueryClient.query(queryConfig);
			result.iterateAll().forEach(row -> {
				String record = row.get(0).getValue().toString();
				JSONObject jsonObject = new JSONObject(record);
				jsonArray.put(jsonObject);
			});
		} catch (JobException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

	@Override
	public boolean insertRecordsFromJson(String table, JSONArray jsonArray) {
		BigQueryWriteSettings bigQueryWriteSettings;
		try {
			bigQueryWriteSettings = BigQueryWriteSettings.newBuilder()
					.setCredentialsProvider(FixedCredentialsProvider.create(this.credentials)).build();
			BigQueryWriteClient bigQueryWriteClient = BigQueryWriteClient.create(bigQueryWriteSettings);

			BQCommittedStream.runWriteCommittedStream(bigQueryWriteClient, this.projectId, this.schema, table, jsonArray);
//			BQPendingStream.writePendingStream(bigQueryWriteClient, this.projectId, this.schema, table, jsonArray);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DescriptorValidationException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

}
