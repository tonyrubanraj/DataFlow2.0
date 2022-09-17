/**
 * 
 */
package com.dev.dataflow2.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dev.dataflow2.service.DatabaseService;
import com.dev.dataflow2.service.impl.BigQueryService;
import com.dev.dataflow2.service.impl.MySQLService;

/**
 * @author tonyr
 *
 */
public class DatabaseUtils {

	public static DatabaseService getDBService(String dbType) {
		switch (dbType) {
		case "mysql": {
			return new MySQLService();
		}
		case "aws_mysql": {
			return new MySQLService();
		}
		case "bigquery": {
			return new BigQueryService();
		}
		default:
			return new MySQLService();
		}
	}

	public static List<String> convertColumnToList(ResultSet resultSet, String columnName) {
		List<String> column = new ArrayList<String>();
		if (resultSet != null) {
			try {
				while (resultSet.next()) {
					column.add(resultSet.getString(columnName));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return column;
	}

	public static JSONArray convertResultSetToJson(ResultSet resultSet) {
		JSONArray jsonArray = new JSONArray();
		try {
			while (resultSet.next()) {
				int columnCount = resultSet.getMetaData().getColumnCount();
				JSONObject jsonObj = new JSONObject();
				for (int col = 1; col <= columnCount; col++) {
					jsonObj.put(resultSet.getMetaData().getColumnName(col), resultSet.getObject(col));
				}
				jsonArray.put(jsonObj);
			}
			return jsonArray;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
