/**
 * 
 */
package com.dev.dataflow2.utils;

import com.dev.dataflow2.service.DatabaseService;
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
		default:
			return new MySQLService();
		}
	}

//	public static List<String> convertColumnToList(ResultSet resultSet, String columnName) {
//		List<String> column = new ArrayList<String>();
//		if (resultSet != null) {
//			try {
//				while (resultSet.next()) {
//					column.add(resultSet.getString(columnName));
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		return column;
//	}
//	
//	public static String convertToString(ResultSet resultSet) {
//		JSONArray jsonArray = new JSONArray();
//		try {
//			while (resultSet.next()) {
//				int columnCount = resultSet.getMetaData().getColumnCount();
//				JSONObject jsonObj = new JSONObject();
//				for(int col = 1; col <= columnCount; col++) {
//					jsonObj.put(resultSet.getMetaData().getColumnName(col), resultSet.getObject(col));
//				}
//				jsonArray.put(jsonObj);
//			}
//			return jsonArray.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
