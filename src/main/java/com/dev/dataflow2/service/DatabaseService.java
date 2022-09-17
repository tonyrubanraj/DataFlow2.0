/**
 * 
 */
package com.dev.dataflow2.service;

import java.sql.Connection;
import java.util.List;

import org.json.JSONArray;

import com.dev.dataflow2.dto.DBConnectionsDto;

/**
 * @author tonyr
 *
 */
public abstract class DatabaseService {

	protected Connection connection;
	protected String schema;

	public Connection getConnection() {
		return connection;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public abstract boolean connect(DBConnectionsDto dbConnection);

	public abstract void close();

	public abstract List<String> getSchemas();

	public abstract List<String> getTables();

	public abstract JSONArray getRecordsAsJson(String table);

	public abstract boolean insertRecordsFromJson(String table, JSONArray jsonArray);

}
