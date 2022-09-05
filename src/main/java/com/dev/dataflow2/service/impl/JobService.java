/**
 * 
 */
package com.dev.dataflow2.service.impl;

import java.util.List;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.dao.UserConnectionsDao;
import com.dev.dataflow2.dto.JobDto;
import com.dev.dataflow2.model.DBConnection;
import com.dev.dataflow2.model.UserConnections;
import com.dev.dataflow2.service.DatabaseService;
import com.dev.dataflow2.utils.DatabaseUtils;

/**
 * @author tonyr
 *
 */
@Service
public class JobService {
	@Autowired
	UserConnectionsDao userConnectionsDao;

	public boolean executeMigration(int userId, JobDto jobDto) {
		UserConnections userConnection = userConnectionsDao.getUserConnectionById(jobDto.getConnectionId());
		DBConnection source = userConnection.getSourceConnection();
		DBConnection destination = userConnection.getDestinationConnection();
		DatabaseService sourceDBService = DatabaseUtils.getDBService(source.getDbType());
		DatabaseService destinationDBService = DatabaseUtils.getDBService(destination.getDbType());
		List<String> sourceTables = jobDto.getSourceTables();
		List<String> destinationTables = jobDto.getDestinationTables();
		if (sourceDBService.connect(source.toDBConnectionDto()) != null
				&& destinationDBService.connect(destination.toDBConnectionDto()) != null
				&& sourceTables.size() == destinationTables.size()) {
			sourceDBService.setSchema(jobDto.getSourceSchema());
			destinationDBService.setSchema(jobDto.getDestinationSchema());
			for (int i = 0; i < sourceTables.size(); i++) {
				JSONArray sourceRecords = sourceDBService.getRecordsAsJson(sourceTables.get(i));
				destinationDBService.insertRecordsFromJson(destinationTables.get(i), sourceRecords);
			}
			return true;
		}
		return false;
	}
}
