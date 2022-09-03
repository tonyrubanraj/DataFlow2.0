/**
 * 
 */
package com.dev.dataflow2.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.dao.UserDao;
import com.dev.dataflow2.exceptions.UnauthorizedUsageException;
import com.dev.dataflow2.exceptions.ValueAlreadyExistsException;
import com.dev.dataflow2.exceptions.ValueNotFoundException;
import com.dev.dataflow2.model.User;
import com.dev.dataflow2.utils.PasswordUtils;

/**
 * @author tonyr
 *
 */
@Service
public class UserAuthenticationService {

	@Autowired
	UserDao userDao;

	private static final int SALT_LENGTH = 30;

	public int authenticateUser(String email, String password) {
		User currentUser = userDao.getUserByEmail(email);
		if (currentUser == null) {
			throw new ValueNotFoundException("User with email ::: " + email + " does not exist");
		}
		byte[] salt = currentUser.getSalt().getBytes();
		if (!PasswordUtils.verifyPassword(password, currentUser.getPassword(), salt)) {
			throw new UnauthorizedUsageException("Incorrect password for user ::: " + email);
		}
		return currentUser.getUserid();
	}

	public int registerUser(User newUser) {
		List<User> users = userDao.getUsers();
		if (!users.isEmpty() && users.stream().anyMatch(user -> user.getEmail().equals(newUser.getEmail()))) {
			throw new ValueAlreadyExistsException("User with email ::: " + newUser.getEmail() + " already exists");
		}
		String salt = PasswordUtils.getSalt(SALT_LENGTH);
		newUser.setSalt(new String(salt));
		newUser.setPassword(PasswordUtils.encryptPassword(newUser.getPassword(), salt.getBytes()));
		return userDao.createUser(newUser);
	}
}
