/**
 * 
 */
package com.dev.dataflow2.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.model.TransferJobs;
import com.dev.dataflow2.service.TransferJobsRepository;

/**
 * @author tonyr
 *
 */
@Service
public class TransferJobsDao {

	@Autowired
	TransferJobsRepository transferJobsRepository;

	public int createTransferJob(TransferJobs transferJob) {
		return transferJobsRepository.save(transferJob).getJobId();
	}

	public TransferJobs getTransferJobById(int id) {
		return transferJobsRepository.findById(id).get();
	}

	public List<TransferJobs> getTransferJobs() {
		List<TransferJobs> transferJobs = new ArrayList<TransferJobs>();
		transferJobsRepository.findAll().forEach(transferJob -> transferJobs.add(transferJob));
		return transferJobs;
	}

	public List<TransferJobs> getTransferJobsByUserId(int userId) {
		List<TransferJobs> transferJobs = new ArrayList<TransferJobs>();
		getTransferJobs().forEach(job -> {
			if (job.getUser().getUserid() == userId) {
				transferJobs.add(job);
			}
		});
		return transferJobs;
	}

	public int updateTransferJob(TransferJobs transferJob) {
		return transferJobsRepository.save(transferJob).getJobId();
	}

}
