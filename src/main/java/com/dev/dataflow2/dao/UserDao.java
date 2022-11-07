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

	public int create(User user) {
		return userRepository.save(user).getUserid();
	}

	public User getById(int id) {
		return userRepository.findById(id).get();
	}

	public User getByEmail(String email) {
		List<User> users = getAll();
		User user = users.isEmpty() ? null
				: users.stream().filter(currentUser -> currentUser.getEmail().equals(email)).findFirst().orElse(null);
		return user;
	}

	public List<User> getAll() {
		List<User> users = new ArrayList<User>();
		userRepository.findAll().forEach(user -> users.add(user));
		return users;
	}

	public User update(User user) {
		return userRepository.save(user);
	}

}
