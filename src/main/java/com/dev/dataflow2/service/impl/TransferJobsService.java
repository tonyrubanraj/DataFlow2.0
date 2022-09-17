/**
 * 
 */
package com.dev.dataflow2.service.impl;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.dao.DBConnectionsDao;
import com.dev.dataflow2.dao.TransferJobsDao;
import com.dev.dataflow2.dao.UserDao;
import com.dev.dataflow2.dto.TransferJobsDto;
import com.dev.dataflow2.model.DBConnections;
import com.dev.dataflow2.model.TransferJobs;
import com.dev.dataflow2.service.DatabaseService;
import com.dev.dataflow2.utils.DatabaseUtils;

/**
 * @author tonyr
 *
 */
@Service
public class TransferJobsService {
	@Autowired
	DBConnectionsDao dbConnectionsDao;

	@Autowired
	TransferJobsDao transferJobsDao;

	@Autowired
	UserDao userDao;

	public boolean executeMigration(int userId, TransferJobsDto transferJobsDto) {
		DBConnections source = dbConnectionsDao.getDBConnectionById(transferJobsDto.getSourceId());
		DBConnections destination = dbConnectionsDao.getDBConnectionById(transferJobsDto.getDestinationId());
		DatabaseService sourceDBService = DatabaseUtils.getDBService(source.getDbType());
		DatabaseService destinationDBService = DatabaseUtils.getDBService(destination.getDbType());
		List<String> sourceTables = transferJobsDto.getSourceTables();
		List<String> destinationTables = transferJobsDto.getDestinationTables();
		if (sourceDBService.connect(source.toDBConnectionsDto())
				&& destinationDBService.connect(destination.toDBConnectionsDto())
				&& sourceTables.size() == destinationTables.size()) {
			sourceDBService.setSchema(transferJobsDto.getSourceSchema());
			destinationDBService.setSchema(transferJobsDto.getDestinationSchema());
			int jobId = createTransferJob(userId, transferJobsDto);
			boolean jobStatus = true;
			for (int i = 0; i < sourceTables.size(); i++) {
				JSONArray sourceRecords = sourceDBService.getRecordsAsJson(sourceTables.get(i));
				jobStatus = jobStatus
						&& destinationDBService.insertRecordsFromJson(destinationTables.get(i), sourceRecords);
			}
			if (jobStatus)
				updateJob(jobId, "Completed");
			else
				updateJob(jobId, "Failed");
			return true;
		}
		return false;
	}

	public int createTransferJob(int userId, TransferJobsDto transferJobsDto) {
		TransferJobs transferJob = new TransferJobs();
		transferJob.setUser(userDao.getUserById(userId));
		transferJob.setSourceDBConnection(dbConnectionsDao.getDBConnectionById(transferJobsDto.getSourceId()));
		transferJob
				.setDestinationDBConnection(dbConnectionsDao.getDBConnectionById(transferJobsDto.getDestinationId()));
		transferJob.setSourceDatabase(transferJobsDto.getSourceSchema());
		transferJob.setDestinationDatabase(transferJobsDto.getDestinationSchema());
		transferJob.setSourceTables(transferJobsDto.getSourceTables().toString());
		transferJob.setDestinationTables(transferJobsDto.getDestinationTables().toString());
		transferJob.setJobType(transferJobsDto.getJobType());
		transferJob.setStatus("Started");
		transferJob.setCreatedTimestamp(new Date());
		return transferJobsDao.createTransferJob(transferJob);
	}

	public int updateJob(int jobId, String status) {
		TransferJobs transferJob = transferJobsDao.getTransferJobById(jobId);
		transferJob.setStatus(status);
		transferJob.setCompletedTimestamp(new Date());
		return transferJobsDao.updateTransferJob(transferJob);
	}

	public JSONArray getTransferJobs(int userId) {
		List<TransferJobs> transferJobs = transferJobsDao.getTransferJobsByUserId(userId);
		JSONArray jsonArray = new JSONArray();
		transferJobs.forEach(transferJob -> {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", transferJob.getJobId());
			jsonObject.put("sourceConnectionName", transferJob.getSourceDBConnection().getConnectionName());
			jsonObject.put("destinationConnectionName", transferJob.getDestinationDBConnection().getConnectionName());
			jsonObject.put("type", transferJob.getJobType());
			jsonObject.put("status", transferJob.getStatus());
			jsonObject.put("createdTime", transferJob.getCreatedTimestamp());
			jsonObject.put("completedTime", transferJob.getCompletedTimestamp());
			jsonArray.put(jsonObject);
		});
		return jsonArray;
	}
}
