package com.dev.dataflow2.dto;

import java.io.Serializable;

/**
 * @author tonyr
 *
 */
public class DBConnectionDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String dbType;
	private String url;
	private String username;
	private String password;

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public DBConnectionDto(String dbType, String url, String username, String password) {
		super();
		this.dbType = dbType;
		this.url = url;
		this.username = username;
		this.password = password;
	}

}
