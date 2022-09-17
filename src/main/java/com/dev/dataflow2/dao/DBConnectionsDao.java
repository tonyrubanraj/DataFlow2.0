/**
 * 
 */
package com.dev.dataflow2.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.model.DBConnections;
import com.dev.dataflow2.service.DBConnectionsRepository;

/**
 * @author tonyr
 *
 */
@Service
public class DBConnectionsDao {

	@Autowired
	DBConnectionsRepository dbConnectionsRepository;

	public int createDBConnection(DBConnections dbConnections) {
		return dbConnectionsRepository.save(dbConnections).getConnectionId();
	}

	public DBConnections getDBConnectionById(int id) {
		return dbConnectionsRepository.findById(id).get();
	}

	public DBConnections getDBConnectionsByName(int userId, String connectionName) {
		List<DBConnections> dbConnections = getDBConnectionsByUserId(userId);
		return dbConnections.isEmpty() ? null
				: dbConnections.stream().filter(dbConnection -> dbConnection.getConnectionName().equals(connectionName))
						.findFirst().orElse(null);
	}

	public boolean isDBConnectionExist(int userId, String connectionName) {
		List<DBConnections> dbConnections = getDBConnectionsByUserId(userId);
		if (!dbConnections.isEmpty() && dbConnections.stream()
				.anyMatch(dbConnection -> dbConnection.getConnectionName().equals(connectionName))) {
			return true;
		}
		return false;
	}

	public List<DBConnections> getDBConnections() {
		List<DBConnections> dbConnections = new ArrayList<DBConnections>();
		dbConnectionsRepository.findAll().forEach(dbConnection -> dbConnections.add(dbConnection));
		return dbConnections;
	}

	public List<DBConnections> getDBConnectionsByUserId(int userId) {
		List<DBConnections> dbConnections = new ArrayList<DBConnections>();
		dbConnectionsRepository.findAll().forEach(dbConnection -> {
			if (dbConnection.getUser().getUserid() == userId)
				dbConnections.add(dbConnection);
		});
		return dbConnections;
	}

	public DBConnections updateDBConnection(DBConnections dbConnection) {
		return dbConnectionsRepository.save(dbConnection);
	}

}
