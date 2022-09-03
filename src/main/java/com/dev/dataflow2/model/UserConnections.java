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
@Table(name = "user_connections")
public class UserConnections {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int connectionid;

	@ManyToOne
	@JoinColumn(name = "userid")
	private User user;

	@ManyToOne
	@JoinColumn(name = "source_db_id")
	private DBConnection sourceConnection;

	@ManyToOne
	@JoinColumn(name = "destination_db_id")
	private DBConnection destinationConnection;

	@Column(name = "connectionname")
	private String connectionName;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public DBConnection getSourceConnection() {
		return sourceConnection;
	}

	public void setSourceConnection(DBConnection sourceConnection) {
		this.sourceConnection = sourceConnection;
	}

	public DBConnection getDestinationConnection() {
		return destinationConnection;
	}

	public void setDestinationConnection(DBConnection destinationConnection) {
		this.destinationConnection = destinationConnection;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public int getConnectionid() {
		return connectionid;
	}

	public UserConnections(User user, DBConnection sourceConnection, DBConnection destinationConnection,
			String connectionName) {
		this.user = user;
		this.sourceConnection = sourceConnection;
		this.destinationConnection = destinationConnection;
		this.connectionName = connectionName;
	}

	public UserConnections() {
		super();
	}

}
