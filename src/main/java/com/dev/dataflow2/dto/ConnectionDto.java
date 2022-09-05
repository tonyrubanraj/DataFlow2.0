package com.dev.dataflow2.dto;

import java.io.Serializable;

public class ConnectionDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int connectionId;
	private String connectionName;

	public int getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(int connectionId) {
		this.connectionId = connectionId;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public ConnectionDto(int connectionId, String connectionName) {
		super();
		this.connectionId = connectionId;
		this.connectionName = connectionName;
	}

	public ConnectionDto() {
		super();
	}

}
