package com.dev.dataflow2.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dev.dataflow2.dao.DBConnectionsDao;
import com.dev.dataflow2.dto.TransferJobsDto;
import com.dev.dataflow2.model.DBConnections;
import com.dev.dataflow2.pojo.MySQLParameters;
import com.dev.dataflow2.service.DatabaseCDCService;
import com.dev.dataflow2.service.DatabaseService;
import com.dev.dataflow2.utils.DatabaseUtils;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;

public class MySQLCDCService extends DatabaseCDCService {

	BinaryLogClient client = null;

	public void setProducerClient(DBConnectionsDao dbConnectionsDao, int connectionId) {
		DBConnections source = dbConnectionsDao.getDBConnectionById(connectionId);
		MySQLParameters mySQLParameters = (MySQLParameters) DatabaseUtils.getConnectionParamsMap(source.getDbType(),
				source.getConnectionParameters());
		String[] urlParts = mySQLParameters.getUrl().split("jdbc:mysql://");
		String[] url = urlParts.length > 0 ? urlParts[1].split(":") : urlParts;
		String hostname = url.length > 1 ? url[0] : "localhost";
		int port = url.length > 1 ? Integer.parseInt(url[1]) : 3306;
		String username = mySQLParameters.getUsername();
		String password = mySQLParameters.getPassword();
		this.client = new BinaryLogClient(hostname, port, username, password);
		EventDeserializer eventDeserializer = new EventDeserializer();
		eventDeserializer.setCompatibilityMode(EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG);
		this.client.setEventDeserializer(eventDeserializer);
	}

	@Override
	public void produceStreamingData(DBConnectionsDao dbConnectionsDao, TransferJobsDto transferJobsDto) {
		try {
			setProducerClient(dbConnectionsDao, transferJobsDto.getSourceId());
			DBConnections destination = dbConnectionsDao.getDBConnectionById(transferJobsDto.getDestinationId());
			DatabaseService destinationDBService = DatabaseUtils.getDBService(destination.getDbType());
			List<String> destinationTables = transferJobsDto.getDestinationTables();
			if (!destinationDBService.connect(destination.toDBConnectionsDto()))
				return;
			destinationDBService.setSchema(transferJobsDto.getDestinationSchema());
			client.registerEventListener(new EventListener() {

				String table = null;
				String database = null;
				List<String> columns = new ArrayList<String>();

				@Override
				public void onEvent(Event event) {
					EventData data = event.getData();
					if (data instanceof TableMapEventData) {
						TableMapEventData tableData = (TableMapEventData) data;
						String currentTable = tableData.getTable();
						String currentDatabase = tableData.getDatabase();
						if (transferJobsDto.getSourceSchema().equals(currentDatabase)
								&& transferJobsDto.getSourceTables().contains(currentTable)) {
							database = currentDatabase;
							table = currentTable;
							columns = tableData.getEventMetadata().getColumnNames();
						} else {
							database = null;
							table = null;
						}
					}
					if (data instanceof WriteRowsEventData) {
						if (table != null && database != null) {
							WriteRowsEventData eventData = (WriteRowsEventData) data;
							JSONArray jsonArr = new JSONArray();
							for (Object[] courseDetails : eventData.getRows()) {
								JSONObject jsonObject = new JSONObject();
								for (int index = 0; index < columns.size(); index++) {
									jsonObject.put(columns.get(index), String.valueOf(courseDetails[index]));
								}
								jsonArr.put(jsonObject);
							}
							destinationDBService.insertRecordsFromJson(destinationTables.get(0), jsonArr);
						}
					}
				}
			});
			client.connect();
		} catch (

		IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopStreamingData() {
		if (client != null) {
			try {
				client.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void consumeStreamingData() {
		// TODO Auto-generated method stub

	}
}
