/**
 * 
 */
package com.dev.dataflow2.dto;

/**
 * @author tonyr
 *
 */
public class UserConnectionsDto {

	private String connectionName;
	private DBConnectionDto sourceConnection;
	private DBConnectionDto destinationConnection;

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public DBConnectionDto getSourceConnection() {
		return sourceConnection;
	}

	public void setSourceConnection(DBConnectionDto sourceConnection) {
		this.sourceConnection = sourceConnection;
	}

	public DBConnectionDto getDestinationConnection() {
		return destinationConnection;
	}

	public void setDestinationConnection(DBConnectionDto destinationConnection) {
		this.destinationConnection = destinationConnection;
	}

	public UserConnectionsDto(String connectionName, DBConnectionDto sourceConnection,
			DBConnectionDto destinationConnection) {
		super();
		this.connectionName = connectionName;
		this.sourceConnection = sourceConnection;
		this.destinationConnection = destinationConnection;
	}

}
