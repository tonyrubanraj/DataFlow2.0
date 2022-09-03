/**
 * 
 */
package com.dev.dataflow2.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import com.dev.dataflow2.dto.DBConnectionDto;

/**
 * @author tonyr
 *
 */
public abstract class DatabaseService {

	protected Connection connection;
	protected String database;

	public Connection getConnection() {
		return connection;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public abstract Connection connect(DBConnectionDto dbConnection);
	
	public abstract void close();

	public abstract List<String> getDatabases();

	public abstract List<String> getTables();

	public abstract String getRecords(String table);

	public abstract boolean insertRecords(String table, ResultSet resultSet);

	public abstract boolean migrateRecords(DatabaseService source, String sourceTable, String targetTable);

}
