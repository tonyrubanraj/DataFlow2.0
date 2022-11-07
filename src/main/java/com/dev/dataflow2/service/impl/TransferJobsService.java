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
	DBConnectionsDao dbConnections;

	@Autowired
	TransferJobsDao transferJobs;

	@Autowired
	UserDao users;

	public boolean executeMigration(int userId, TransferJobsDto transferJobsDto) {
		DBConnections source = dbConnections.getById(transferJobsDto.getSourceId());
		DBConnections destination = dbConnections.getById(transferJobsDto.getDestinationId());
		DatabaseService sourceDBService = DatabaseUtils.getDBServices().get(source.getDbType());
		DatabaseService destinationDBService = DatabaseUtils.getDBServices().get(destination.getDbType());
		List<String> sourceTables = transferJobsDto.getSourceTables();
		List<String> destinationTables = transferJobsDto.getDestinationTables();
		if (sourceDBService.connect(source.getConnectionParameters())
				&& destinationDBService.connect(destination.getConnectionParameters())
				&& sourceTables.size() == destinationTables.size()) {
			int jobId = createTransferJob(userId, transferJobsDto, "Started");
			boolean jobStatus = true;
			for (int i = 0; i < sourceTables.size(); i++) {
				JSONArray sourceRecords = sourceDBService.getRecordsAsJson(transferJobsDto.getSourceSchema(), sourceTables.get(i));
				jobStatus = jobStatus
						&& destinationDBService.insertRecordsFromJson(transferJobsDto.getDestinationSchema(), destinationTables.get(i), sourceRecords);
			}
			if (jobStatus)
				updateJob(jobId, "Completed");
			else
				updateJob(jobId, "Failed");
			return true;
		}
		return false;
	}

	public boolean executeCDC(int userId, TransferJobsDto transferJobsDto) {
		CDCService cdcService = new CDCService(dbConnections, transferJobsDto);
		cdcService.setName("1");
		cdcService.start();
		return true;
	}

	public boolean stopCDC() {
		for (Thread currentThread : Thread.getAllStackTraces().keySet()) {
			if (currentThread.getName().equals("1")) {
				System.out.println("currently active thread found");
				currentThread.interrupt();
			}
		}
		return true;
	}

	public int createTransferJob(int userId, TransferJobsDto transferJobsDto, String status) {
		TransferJobs transferJob = new TransferJobs();
		transferJob.setUser(users.getById(userId));
		transferJob.setSourceDBConnection(dbConnections.getById(transferJobsDto.getSourceId()));
		transferJob
				.setDestinationDBConnection(dbConnections.getById(transferJobsDto.getDestinationId()));
		transferJob.setSourceDatabase(transferJobsDto.getSourceSchema());
		transferJob.setDestinationDatabase(transferJobsDto.getDestinationSchema());
		transferJob.setSourceTables(transferJobsDto.getSourceTables().toString());
		transferJob.setDestinationTables(transferJobsDto.getDestinationTables().toString());
		transferJob.setJobType(transferJobsDto.getJobType());
		transferJob.setStatus(status);
		transferJob.setCreatedTimestamp(new Date());
		return transferJobs.create(transferJob);
	}

	public int updateJob(int jobId, String status) {
		TransferJobs transferJob = transferJobs.getById(jobId);
		transferJob.setStatus(status);
		transferJob.setCompletedTimestamp(new Date());
		return transferJobs.update(transferJob);
	}

	public JSONArray getTransferJobs(int userId) {
		List<TransferJobs> jobs = transferJobs.getByUserId(userId);
		JSONArray jsonArray = new JSONArray();
		jobs.forEach(transferJob -> {
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
