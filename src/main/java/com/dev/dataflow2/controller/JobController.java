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

import com.dev.dataflow2.dto.JobDto;
import com.dev.dataflow2.service.impl.JobService;
import com.dev.dataflow2.utils.Constants;

@RestController
@RequestMapping(path = "/job")
public class JobController {

	@Autowired
	JobService jobService;

	@PostMapping(path = "/migrate")
	public ResponseEntity<String> migrate(@RequestBody JobDto jobDto, HttpSession session) {
		int userId = (int) session.getAttribute(Constants.USER_ID);
		if (jobService.executeMigration(userId, jobDto))
			return new ResponseEntity<String>("successfully initiated migration job", HttpStatus.OK);
		else {
			int jobId = jobService.createJob(jobDto);
			jobService.updateJob(jobId, "Failed");
			return new ResponseEntity<String>("Error in initiating migration job", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(path = "/list")
	public String fetchJobs(HttpSession session) {
		int userId = (int) session.getAttribute(Constants.USER_ID);
		try {
			JSONArray jsonArray = jobService.getJobs(userId);
			return jsonArray.toString();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
}
