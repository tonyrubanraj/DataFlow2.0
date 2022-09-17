package com.dev.dataflow2.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author tonyr
 *
 */
public class DBConnectionsDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String connectionName;
	private String dbType;

	@JsonProperty("connectionParameters")
	private JsonNode connectionParameters;

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getConnectionParameters() {
		return connectionParameters != null ? connectionParameters.toString() : null;
	}

	public void setConnectionParameters(JsonNode connectionParameters) {
		this.connectionParameters = connectionParameters;
	}

	public DBConnectionsDto() {
		super();
	}

	public DBConnectionsDto(String connectionName, String dbType, JsonNode connectionParameters) {
		this.connectionName = connectionName;
		this.dbType = dbType;
		this.connectionParameters = connectionParameters;
	}

	public DBConnectionsDto(String connectionName, String dbType, String connectionParameters) {
		this.connectionName = connectionName;
		this.dbType = dbType;

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = null;
		try {
			jsonNode = mapper.readTree(connectionParameters);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		this.connectionParameters = jsonNode;
	}

	@Override
	public String toString() {
		return "DBConnectionsDto [connectionName=" + connectionName + ", dbType=" + dbType + ", connectionParameters="
				+ connectionParameters + "]";
	}

}
