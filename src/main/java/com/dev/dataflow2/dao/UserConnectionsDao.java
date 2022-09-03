/**
 * 
 */
package com.dev.dataflow2.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.model.UserConnections;
import com.dev.dataflow2.service.UserConnectionsRepository;

/**
 * @author tonyr
 *
 */
@Service
public class UserConnectionsDao {

	@Autowired
	UserConnectionsRepository userConnectionsRepository;

	public int createUserConnection(UserConnections userConnection) {
		return userConnectionsRepository.save(userConnection).getConnectionid();
	}

	public UserConnections getUserConnectionById(int id) {
		return userConnectionsRepository.findById(id).get();
	}

	public List<UserConnections> getUserConnectionsByUserId(int userId) {
		List<UserConnections> userConnections = new ArrayList<UserConnections>();
		userConnectionsRepository.findAll().forEach(userConnection -> {
			if (userConnection.getUser().getUserid() == userId)
				userConnections.add(userConnection);
		});
		return userConnections;
	}

	public UserConnections getUserConnectionsByName(int userId, String connectionName) {
		List<UserConnections> connections = getUserConnectionsByUserId(userId);
		return connections.isEmpty() ? null
				: connections.stream().filter(connection -> connection.getConnectionName().equals(connectionName))
						.findFirst().orElse(null);
	}

	public boolean isConnectionExist(int userId, String connectionName) {
		List<UserConnections> connections = getUserConnectionsByUserId(userId);
		if (!connections.isEmpty()
				&& connections.stream().anyMatch(connection -> connection.getConnectionName().equals(connectionName))) {
			return true;
		}
		return false;
	}

	public List<UserConnections> getUserConnections() {
		List<UserConnections> userConnections = new ArrayList<UserConnections>();
		userConnectionsRepository.findAll().forEach(userConnection -> userConnections.add(userConnection));
		return userConnections;
	}

	public UserConnections updateUserConnection(UserConnections userConnections, int id) {
		return userConnectionsRepository.save(userConnections);
	}

}
