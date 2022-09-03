/**
 * 
 */
package com.dev.dataflow2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dev.dataflow2.dto.DBConnectionDto;

/**
 * @author tonyr
 *
 */
@Entity
@Table(name = "db_connection")
public class DBConnection {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int dbid;

	@Column(name = "dbtype")
	private String dbType;
	@Column
	private String url;
	@Column
	private String username;
	@Column
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

	public int getDbid() {
		return dbid;
	}

	public DBConnection(String dbType, String url, String username, String password) {
		this.dbType = dbType;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public DBConnection(DBConnectionDto dbConnection) {
		this.dbType = dbConnection.getDbType();
		this.url = dbConnection.getUrl();
		this.username = dbConnection.getUsername();
		this.password = dbConnection.getPassword();
	}

	public DBConnection() {
		super();
	}

}
