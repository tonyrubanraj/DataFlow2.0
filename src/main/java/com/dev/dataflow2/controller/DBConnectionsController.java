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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dataflow2.dto.DBConnectionsDto;
import com.dev.dataflow2.service.DatabaseService;
import com.dev.dataflow2.service.impl.DBConnectionService;
import com.dev.dataflow2.utils.Constants;
import com.dev.dataflow2.utils.DatabaseUtils;

/**
 * @author tonyr
 *
 */
@RestController
@RequestMapping("/connection")
public class DBConnectionsController {

	@Autowired
	DBConnectionService dbConnectionService;

	@PostMapping(path = "/test")
	public ResponseEntity<String> testDBConnection(@RequestBody DBConnectionsDto dbConnection) {
		DatabaseService dbService = DatabaseUtils.getDBService(dbConnection.getDbType());
		if (!dbService.connect(dbConnection)) {
			return new ResponseEntity<>("DB connection failure", HttpStatus.BAD_REQUEST);
		}
		dbService.close();
		return new ResponseEntity<>("DB connection successful", HttpStatus.OK);
	}

	@PostMapping(path = "/save")
	public ResponseEntity<String> saveDBConnection(@RequestBody DBConnectionsDto dbConnection, HttpSession session) {
		dbConnectionService.saveDBConnections(dbConnection, (int) session.getAttribute(Constants.USER_ID));
		return new ResponseEntity<>("DB Connection saved successfully", HttpStatus.OK);
	}

	@GetMapping(path = "/list")
	public String fetchConnections(HttpSession session) {
		int userId = (int) session.getAttribute(Constants.USER_ID);
		try {
			JSONArray jsonArray = dbConnectionService.getDBConnections(userId);
			return jsonArray.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@GetMapping(path = "/schemas")
	public List<String> fetchSchemas(@RequestParam int connectionId) {
		if (connectionId == -1)
			return new ArrayList<String>();
		return dbConnectionService.getSchemas(connectionId);
	}

	@GetMapping(path = "/tables")
	public List<String> fetchTables(@RequestParam int connectionId, @RequestParam String schema) {
		if (connectionId == -1)
			return new ArrayList<String>();
		return dbConnectionService.getTables(connectionId, schema);
	}
}
