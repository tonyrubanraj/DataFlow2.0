/**
 * 
 */
package com.dev.dataflow2.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.model.DBConnection;
import com.dev.dataflow2.service.DBConnectionRepository;

/**
 * @author tonyr
 *
 */
@Service
public class DBConnectionDao {

	@Autowired
	DBConnectionRepository dbConnectionRepository;

	public int createDBConnection(DBConnection dbConnection) {
		return dbConnectionRepository.save(dbConnection).getDbid();
	}

	public DBConnection getDBConnectionById(int id) {
		return dbConnectionRepository.findById(id).get();
	}

	public List<DBConnection> getDBConnections() {
		List<DBConnection> dbConnections = new ArrayList<DBConnection>();
		dbConnectionRepository.findAll().forEach(dbConnection -> dbConnections.add(dbConnection));
		return dbConnections;
	}

	public DBConnection updateDBConnection(DBConnection dbConnection, int id) {
		return dbConnectionRepository.save(dbConnection);
	}

}
