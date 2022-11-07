package com.dev.dataflow2.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.dao.DBConnectionsDao;
import com.dev.dataflow2.dao.UserDao;
import com.dev.dataflow2.dto.DBConnectionsDto;
import com.dev.dataflow2.exceptions.ValueAlreadyExistsException;
import com.dev.dataflow2.model.DBConnections;
import com.dev.dataflow2.service.DatabaseService;
import com.dev.dataflow2.utils.DatabaseUtils;

@Service
public class DBConnectionService {

	@Autowired
	private DBConnectionsDao dbConnections;

	@Autowired
	private UserDao users;

	@Transactional
	public int saveDBConnections(DBConnectionsDto dbConnectionsDto, int userId) {
		String connectionName = dbConnectionsDto.getConnectionName();
		if (dbConnections.exists(userId, connectionName)) {
			throw new ValueAlreadyExistsException(
					"Connection name ::: " + connectionName + " already exists for the user");
		}
		try {
			DBConnections dbConnection = new DBConnections(dbConnectionsDto.getConnectionName(),
					users.getById(userId), dbConnectionsDto.getDbType(),
					dbConnectionsDto.getConnectionParameters());
			return dbConnections.create(dbConnection);
		} catch (Exception e) {
			throw new RuntimeException("Error in saving the connection parameters for the user");
		}
	}

	public JSONArray getDBConnections(int userId) {
		List<DBConnections> connections = dbConnections.getByUserId(userId);
		JSONArray jsonArray = new JSONArray();
		connections.forEach(dbConnection -> {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", dbConnection.getConnectionId());
			jsonObject.put("name", dbConnection.getConnectionName());
			jsonArray.put(jsonObject);
		});
		return jsonArray;
	}

	public List<String> getSchemas(int connectionId) {
		List<String> schemas = new ArrayList<String>();
		try {
			DBConnections dbConnection = dbConnections.getById(connectionId);
			DatabaseService dbService = DatabaseUtils.getDBServices().get(dbConnection.getDbType());
			if (dbService.connect(dbConnection.getConnectionParameters())) {
				schemas = dbService.getSchemas();
				dbService.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return schemas;
	}

	public List<String> getTables(int connectionId, String schema) {
		List<String> tables = new ArrayList<String>();
		try {
			DBConnections dbConnection = dbConnections.getById(connectionId);
			DatabaseService dbService = DatabaseUtils.getDBServices().get(dbConnection.getDbType());
			if (dbService.connect(dbConnection.getConnectionParameters())) {
				tables = dbService.getTables(schema);
				dbService.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tables;
	}
}
