/**
 * 
 */
package com.dev.dataflow2.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.model.User;
import com.dev.dataflow2.service.UserRepository;

/**
 * @author tonyr
 *
 */
@Service
public class UserDao {

	@Autowired
	UserRepository userRepository;

	public int createUser(User user) {
		return userRepository.save(user).getUserid();
	}

	public User getUserById(int id) {
		return userRepository.findById(id).get();
	}

	public List<User> getUsers() {
		List<User> users = new ArrayList<User>();
		userRepository.findAll().forEach(user -> users.add(user));
		return users;
	}

	public User updateUser(User user, int id) {
		return userRepository.save(user);
	}

}
