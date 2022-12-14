package com.dev.dataflow2.controller;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dataflow2.dto.TransferJobsDto;
import com.dev.dataflow2.service.impl.TransferJobsService;
import com.dev.dataflow2.utils.Constants;

@RestController
@RequestMapping(path = "/job")
public class TransferJobsController {

	@Autowired
	TransferJobsService transferJobService;

	@PostMapping(path = "/migrate")
	public ResponseEntity<String> migrate(@RequestBody TransferJobsDto transferJobDto, HttpSession session) {
		int userId = (int) session.getAttribute(Constants.USER_ID);
		boolean status = true;
		if ("Bulk".equals(transferJobDto.getJobType())) {
			status = transferJobService.executeMigration(userId, transferJobDto);
		} else {
			status = transferJobService.executeCDC(userId, transferJobDto);
		}
		if (status)
			return new ResponseEntity<String>("successfully initiated migration job", HttpStatus.OK);
		else {
			transferJobService.createTransferJob(userId, transferJobDto, "Failed");
			return new ResponseEntity<String>("Error in initiating migration job", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(path = "/cdc/stop")
	public int cdcStop() {
		transferJobService.stopCDC();
		return 1;
	}

	@GetMapping(path = "/list")
	public String fetchJobs(HttpSession session) {
		int userId = (int) session.getAttribute(Constants.USER_ID);
		try {
			JSONArray jsonArray = transferJobService.getTransferJobs(userId);
			return jsonArray.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
