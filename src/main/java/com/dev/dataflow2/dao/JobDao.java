/**
 * 
 */
package com.dev.dataflow2.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.model.Job;
import com.dev.dataflow2.service.JobRepository;

/**
 * @author tonyr
 *
 */
@Service
public class JobDao {

	@Autowired
	JobRepository jobRepository;

	public int createJob(Job job) {
		return jobRepository.save(job).getJobid();
	}

	public Job getJobById(int id) {
		return jobRepository.findById(id).get();
	}

	public List<Job> getJobs() {
		List<Job> jobs = new ArrayList<Job>();
		jobRepository.findAll().forEach(job -> jobs.add(job));
		return jobs;
	}

	public List<Job> getJobsByUserId(int userId) {
		List<Job> jobs = new ArrayList<Job>();
		getJobs().forEach(job -> {
			if (job.getUserConnection().getUser().getUserid() == userId) {
				jobs.add(job);
			}
		});
		return jobs;
	}

	public int updateJob(Job job, int id) {
		return jobRepository.save(job).getJobid();
	}

}
