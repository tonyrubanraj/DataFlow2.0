/**
 * 
 */
package com.dev.dataflow2.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping(path = "/list")
	public String fetchConnections(HttpSession session) {
		int userId = (int) session.getAttribute(Constants.USER_ID);
		try {
			JSONArray jsonArray = userConnectionService.getConnections(userId);
			return jsonArray.toString();
		}catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	@GetMapping(path = "/{connectionType}/schemas")
	public List<String> fetchSchemas(@RequestParam int connectionId,
			@PathVariable(value = "connectionType") String connectionType) {
		if(connectionId == -1)
			return new ArrayList<String>();
		return userConnectionService.getSchemas(connectionId, connectionType);
	}

	@GetMapping(path = "/{connectionType}/tables")
	public List<String> fetchTables(@RequestParam int connectionId, @RequestParam String schema,
			@PathVariable(value = "connectionType") String connectionType) {
		if(connectionId == -1)
			return new ArrayList<String>();
		return userConnectionService.getTables(connectionId, schema, connectionType);
	}
}
