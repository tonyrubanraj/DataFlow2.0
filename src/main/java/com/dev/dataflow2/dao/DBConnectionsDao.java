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

	public int create(DBConnections dbConnections) {
		return dbConnectionsRepository.save(dbConnections).getConnectionId();
	}

	public DBConnections getById(int id) {
		return dbConnectionsRepository.findById(id).get();
	}

	public DBConnections getByName(int userId, String connectionName) {
		List<DBConnections> dbConnections = getByUserId(userId);
		return dbConnections.isEmpty() ? null
				: dbConnections.stream().filter(dbConnection -> dbConnection.getConnectionName().equals(connectionName))
						.findFirst().orElse(null);
	}

	public boolean exists(int userId, String connectionName) {
		List<DBConnections> dbConnections = getByUserId(userId);
		if (!dbConnections.isEmpty() && dbConnections.stream()
				.anyMatch(dbConnection -> dbConnection.getConnectionName().equals(connectionName))) {
			return true;
		}
		return false;
	}

	public List<DBConnections> getAll() {
		List<DBConnections> dbConnections = new ArrayList<DBConnections>();
		dbConnectionsRepository.findAll().forEach(dbConnection -> dbConnections.add(dbConnection));
		return dbConnections;
	}

	public List<DBConnections> getByUserId(int userId) {
		List<DBConnections> dbConnections = new ArrayList<DBConnections>();
		dbConnectionsRepository.findAll().forEach(dbConnection -> {
			if (dbConnection.getUser().getUserid() == userId)
				dbConnections.add(dbConnection);
		});
		return dbConnections;
	}

	public DBConnections update(DBConnections dbConnection) {
		return dbConnectionsRepository.save(dbConnection);
	}

}
