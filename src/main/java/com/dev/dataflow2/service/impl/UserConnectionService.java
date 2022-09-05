/**
 * 
 */
package com.dev.dataflow2.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.dao.DBConnectionDao;
import com.dev.dataflow2.dao.UserConnectionsDao;
import com.dev.dataflow2.dao.UserDao;
import com.dev.dataflow2.dto.UserConnectionsDto;
import com.dev.dataflow2.exceptions.ValueAlreadyExistsException;
import com.dev.dataflow2.model.DBConnection;
import com.dev.dataflow2.model.UserConnections;
import com.dev.dataflow2.service.DatabaseService;
import com.dev.dataflow2.utils.DatabaseUtils;

/**
 * @author tonyr
 *
 */
@Service
public class UserConnectionService {

	@Autowired
	DBConnectionDao dbConnectionDao;

	@Autowired
	UserDao userDao;

	@Autowired
	UserConnectionsDao userConnectionsDao;

	@Transactional
	public int saveUserConnections(UserConnectionsDto userConnectionsDto, int userId) {
		String connectionName = userConnectionsDto.getConnectionName();
		if (userConnectionsDao.isConnectionExist(userId, connectionName)) {
			throw new ValueAlreadyExistsException(
					"Connection name ::: " + connectionName + " already exists for the user");
		}
		try {
			DBConnection sourceConnection = new DBConnection(userConnectionsDto.getSourceConnection());
			DBConnection destinationConnection = new DBConnection(userConnectionsDto.getDestinationConnection());
			int sourceDBId = dbConnectionDao.createDBConnection(sourceConnection);
			int destinationDBId = dbConnectionDao.createDBConnection(destinationConnection);
			UserConnections userConnections = new UserConnections(userDao.getUserById(userId),
					dbConnectionDao.getDBConnectionById(sourceDBId),
					dbConnectionDao.getDBConnectionById(destinationDBId), userConnectionsDto.getConnectionName());
			return userConnectionsDao.createUserConnection(userConnections);
		} catch (Exception e) {
			throw new RuntimeException("Error in saving the connection parameters for the user");
		}
	}

	public List<String> getSchemas(int connectionId, String connectionType) {
		List<String> schemas = new ArrayList<String>();
		try {
			UserConnections userConnection = userConnectionsDao.getUserConnectionById(connectionId);
			DBConnection dbConnection = null;
			if ("source".equalsIgnoreCase(connectionType))
				dbConnection = userConnection.getSourceConnection();
			else
				dbConnection = userConnection.getDestinationConnection();
			DatabaseService dbService = DatabaseUtils.getDBService(dbConnection.getDbType());
			dbService.connect(dbConnection.toDBConnectionDto());
			schemas = dbService.getSchemas();
			dbService.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return schemas;
	}

	public List<String> getTables(int connectionId, String schema, String connectionType) {
		List<String> tables = new ArrayList<String>();
		try {
			UserConnections userConnection = userConnectionsDao.getUserConnectionById(connectionId);
			DBConnection dbConnection = null;
			if ("source".equalsIgnoreCase(connectionType)) {
				dbConnection = userConnection.getSourceConnection();
			} else {
				dbConnection = userConnection.getDestinationConnection();
			}
			DatabaseService dbService = DatabaseUtils.getDBService(dbConnection.getDbType());
			dbService.connect(dbConnection.toDBConnectionDto());
			dbService.setSchema(schema);
			tables = dbService.getTables();
			dbService.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tables;
	}

	public JSONArray getConnections(int userId) {
		List<UserConnections> userConnections = userConnectionsDao.getUserConnections();
		JSONArray jsonArray = new JSONArray();
		userConnections.forEach(userConnection -> {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", userConnection.getConnectionid());
			jsonObject.put("name", userConnection.getConnectionName());
			jsonArray.put(jsonObject);
		});
		return jsonArray;
	}
}
