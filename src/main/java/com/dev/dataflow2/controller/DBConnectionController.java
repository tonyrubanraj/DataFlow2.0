/**
 * 
 */
package com.dev.dataflow2.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dataflow2.dto.DBConnectionDto;
import com.dev.dataflow2.dto.UserConnectionsDto;
import com.dev.dataflow2.service.DatabaseService;
import com.dev.dataflow2.service.impl.UserConnectionService;
import com.dev.dataflow2.utils.Constants;
import com.dev.dataflow2.utils.DatabaseUtils;

/**
 * @author tonyr
 *
 */
@RestController
@RequestMapping("/connection")
public class DBConnectionController {

	@Autowired
	UserConnectionService userConnectionService;

	@PostMapping(path = "/test")
	public ResponseEntity<String> testDBConnection(@RequestBody DBConnectionDto dbConnection) {
		DatabaseService dbService = DatabaseUtils.getDBService(dbConnection.getDbType());
		if (dbService.connect(dbConnection) == null) {
			return new ResponseEntity<>("DB connection failure", HttpStatus.BAD_REQUEST);
		}
		dbService.close();
		return new ResponseEntity<>("DB connection successful", HttpStatus.OK);
	}

	@PostMapping(path = "/save")
	public ResponseEntity<String> saveDBConnection(@RequestBody UserConnectionsDto userConnectionsDto,
			HttpSession session) {
		userConnectionService.saveUserConnections(userConnectionsDto, (int) session.getAttribute(Constants.USER_ID));
		return new ResponseEntity<>("User DB Connection saved successfully", HttpStatus.OK);
	}
}
