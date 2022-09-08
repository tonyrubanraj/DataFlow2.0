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

import com.dev.dataflow2.dao.JobDao;
import com.dev.dataflow2.dao.UserConnectionsDao;
import com.dev.dataflow2.dto.JobDto;
import com.dev.dataflow2.model.DBConnection;
import com.dev.dataflow2.model.Job;
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

	@Autowired
	JobDao jobDao;

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
			int jobId = createJob(jobDto);
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

	public int createJob(JobDto jobDto) {
		Job job = new Job();
		job.setUserConnection(userConnectionsDao.getUserConnectionById(jobDto.getConnectionId()));
		job.setSourceDatabase(jobDto.getSourceSchema());
		job.setDestinationDatabase(jobDto.getDestinationSchema());
		job.setSourceTables(jobDto.getSourceTables().toString());
		job.setDestinationTables(jobDto.getDestinationTables().toString());
		job.setJobType(jobDto.getJobType());
		job.setStatus("Started");
		job.setCreatedTimestamp(new Date());
		return jobDao.createJob(job);
	}

	public int updateJob(int jobId, String status) {
		Job job = jobDao.getJobById(jobId);
		job.setStatus(status);
		job.setCompletedTimestamp(new Date());
		return jobDao.updateJob(job, jobId);
	}

	public JSONArray getJobs(int userId) {
		List<Job> jobs = jobDao.getJobsByUserId(userId);
		JSONArray jsonArray = new JSONArray();
		jobs.forEach(job -> {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", job.getJobid());
			jsonObject.put("connectionName", job.getUserConnection().getConnectionName());
			jsonObject.put("type", job.getJobType());
			jsonObject.put("status", job.getStatus());
			jsonObject.put("createdTime", job.getCreatedTimestamp());
			jsonObject.put("completedTime", job.getCompletedTimestamp());
			jsonArray.put(jsonObject);
		});
		return jsonArray;
	}
}
