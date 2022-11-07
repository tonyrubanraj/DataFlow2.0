package com.dev.dataflow2.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dataflow2.dto.LoginDto;
import com.dev.dataflow2.dto.UserDto;
import com.dev.dataflow2.model.User;
import com.dev.dataflow2.service.impl.UserAuthenticationService;
import com.dev.dataflow2.utils.Constants;

/**
 * @author tonyr
 *
 */
@RestController
public class UserController {

	@Autowired
	UserAuthenticationService authenticationService;

	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody LoginDto loginDto, HttpSession session) {
		int userId = authenticationService.authenticateUser(loginDto.getEmail(), loginDto.getPassword());
		session.setAttribute(Constants.USER_ID, userId);
		return new ResponseEntity<>("login successful", HttpStatus.OK);
	}

	@PostMapping("/signup")
	public ResponseEntity<String> signupUser(@RequestBody UserDto userDto, HttpSession session) {
		User user = new User(userDto);
		int userId = authenticationService.registerUser(user);
		session.setAttribute(Constants.USER_ID, userId);
		return new ResponseEntity<>("Signup completed", HttpStatus.CREATED);
	}

	@PostMapping("/test")
	public ResponseEntity<String> test() {
		return new ResponseEntity<>("TEst successful", HttpStatus.OK);
	}
}
