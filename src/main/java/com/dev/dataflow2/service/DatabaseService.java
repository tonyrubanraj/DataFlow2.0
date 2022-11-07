/**
 * 
 */
package com.dev.dataflow2.service;

import java.util.List;

import org.json.JSONArray;

/**
 * @author tonyr
 *
 */
public abstract class DatabaseService {

	public abstract boolean connect(String connectionParameters);

	public abstract void close();

	public abstract List<String> getSchemas();

	public abstract List<String> getTables(String schema);

	public abstract JSONArray getRecordsAsJson(String schema, String table);

	public abstract boolean insertRecordsFromJson(String schema, String table, JSONArray jsonArray);

}
