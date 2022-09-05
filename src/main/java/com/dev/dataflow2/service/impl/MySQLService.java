/**
 * 
 */
package com.dev.dataflow2.service.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dev.dataflow2.dto.DBConnectionDto;
import com.dev.dataflow2.service.DatabaseService;
import com.dev.dataflow2.utils.Constants;
import com.dev.dataflow2.utils.DatabaseUtils;

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
	public List<String> getSchemas() {
		List<String> schemas = new ArrayList<String>();
		try (Statement statement = connection.createStatement()) {
			ResultSet rs = statement.executeQuery("Show Databases");
			schemas = DatabaseUtils.convertColumnToList(rs, "Database");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return schemas;
	}

	@Override
	public List<String> getTables() {
		List<String> tables = new ArrayList<String>();
		try (Statement statement = connection.createStatement()) {
			String query = "select table_name from information_schema.tables where table_schema = '" + schema + "'";
			ResultSet rs = statement.executeQuery(query);
			tables = DatabaseUtils.convertColumnToList(rs, "table_name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tables;
	}

	@Override
	public JSONArray getRecordsAsJson(String table) {
		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery("Select * from " + schema + "." + table);
			return DatabaseUtils.convertResultSetToJson(resultSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean insertRecordsFromJson(String table, JSONArray jsonArray) {
		try {
			if (jsonArray != null && !jsonArray.isEmpty()) {
				int columnCount = jsonArray.getJSONObject(0).length();
				List<String> columns = new ArrayList<>(jsonArray.getJSONObject(0).keySet());
				String query = String.format(
						"insert into %s.%s(" + columns.stream().collect(Collectors.joining(", ")) + ") values ("
								+ columns.stream().map(column -> "?").collect(Collectors.joining(", ")) + ")",
						schema, table);
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				int recordCount = 0;
				Iterator<Object> jsonIterator = jsonArray.iterator();
				while (jsonIterator.hasNext()) {
					JSONObject jsonObject = (JSONObject) jsonIterator.next();
					for (int i = 0; i < columnCount; i++) {
						preparedStatement.setObject(i + 1, jsonObject.get(columns.get(i)));
					}
					preparedStatement.addBatch();
					recordCount++;
					if (recordCount == 1000000) {
						recordCount = 0;
						preparedStatement.executeBatch();
					}
				}
				preparedStatement.executeBatch();
				preparedStatement.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
