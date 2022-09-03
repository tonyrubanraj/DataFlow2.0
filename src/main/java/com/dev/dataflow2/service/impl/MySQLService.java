/**
 * 
 */
package com.dev.dataflow2.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.dev.dataflow2.dto.DBConnectionDto;
import com.dev.dataflow2.service.DatabaseService;
import com.dev.dataflow2.utils.Constants;

/**
 * @author tonyr
 *
 */
public class MySQLService extends DatabaseService {

	@Override
	public Connection connect(DBConnectionDto dbConnectionDto) {
		try {
			Class.forName(Constants.JDBC_CONNECTION);
			this.connection = DriverManager.getConnection(dbConnectionDto.getUrl(), dbConnectionDto.getUsername(),
					dbConnectionDto.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
			this.connection = null;
		}
		return this.connection;
	}

	@Override
	public void close() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getDatabases() {
		return null;
	}

	@Override
	public List<String> getTables() {
		return null;
	}

	@Override
	public String getRecords(String table) {
		return null;
	}

	@Override
	public boolean insertRecords(String table, ResultSet resultSet) {
		return false;
	}

	@Override
	public boolean migrateRecords(DatabaseService source, String sourceTable, String targetTable) {
		return false;
	}

}
