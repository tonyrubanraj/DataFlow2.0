/**
 * 
 */
package com.dev.dataflow2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author tonyr
 *
 */
@Entity
@Table(name = "db_connections")
public class DBConnections {

	@Id
	@Column(name = "connectionid")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int connectionId;

	@Column(name = "connectionname")
	private String connectionName;

	@ManyToOne
	@JoinColumn(name = "userid")
	private User user;

	@Column(name = "dbtype")
	private String dbType;

	@Column(name = "connectionparameters")
	private String connectionParameters;

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getConnectionParameters() {
		return connectionParameters;
	}

	public void setConnectionParameters(String connectionParameters) {
		this.connectionParameters = connectionParameters;
	}

	public int getConnectionId() {
		return connectionId;
	}

	public DBConnections() {
		super();
	}

	public DBConnections(String connectionName, User user, String dbType, String connectionParameters) {
		super();
		this.connectionName = connectionName;
		this.user = user;
		this.dbType = dbType;
		this.connectionParameters = connectionParameters;
	}

//	public DBConnectionsDto toDBConnectionsDto() {
//		return new DBConnectionsDto(this.connectionName, this.dbType, this.connectionParameters);
//	}

	@Override
	public String toString() {
		return "DBConnections [connectionId=" + connectionId + ", connectionName=" + connectionName + ", user=" + user
				+ ", dbType=" + dbType + ", connectionParameters=" + connectionParameters + "]";
	}

}
