/**
 * 
 */
package com.dev.dataflow2.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.dataflow2.dao.DBConnectionDao;
import com.dev.dataflow2.dao.UserConnectionsDao;
import com.dev.dataflow2.dao.UserDao;
import com.dev.dataflow2.dto.UserConnectionsDto;
import com.dev.dataflow2.exceptions.ValueAlreadyExistsException;
import com.dev.dataflow2.model.DBConnection;
import com.dev.dataflow2.model.UserConnections;

/**
 * @author tonyr
 *
 */
@Service
public class UserConnectionService {

	@Autowired
	DBConnectionDao dbConnectionDao;

	@Autowired
	UserDao userDao;

	@Autowired
	UserConnectionsDao userConnectionsDao;

	@Transactional
	public int saveUserConnections(UserConnectionsDto userConnectionsDto, int userId) {
		String connectionName = userConnectionsDto.getConnectionName();
		if (userConnectionsDao.isConnectionExist(userId, connectionName)) {
			throw new ValueAlreadyExistsException(
					"Connection name ::: " + connectionName + " already exists for the user");
		}
		try {
			DBConnection sourceConnection = new DBConnection(userConnectionsDto.getSourceConnection());
			DBConnection destinationConnection = new DBConnection(userConnectionsDto.getDestinationConnection());
			int sourceDBId = dbConnectionDao.createDBConnection(sourceConnection);
			int destinationDBId = dbConnectionDao.createDBConnection(destinationConnection);
			UserConnections userConnections = new UserConnections(userDao.getUserById(userId),
					dbConnectionDao.getDBConnectionById(sourceDBId),
					dbConnectionDao.getDBConnectionById(destinationDBId), userConnectionsDto.getConnectionName());
			return userConnectionsDao.createUserConnection(userConnections);
		} catch (Exception e) {
			throw new RuntimeException("Error in saving the connection parameters for the user");
		}
	}
}
