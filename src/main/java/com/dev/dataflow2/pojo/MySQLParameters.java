package com.dev.dataflow2.pojo;

public class MySQLParameters {
	private String url;
	private String username;
	private String password;

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

	public MySQLParameters(String url, String username, String password) {
		super();
		this.url = url;
		this.username = username;
		this.password = password;
	}

	@Override
	public String toString() {
		return "MySQLConnectionParameters [url=" + url + ", username=" + username + ", password=" + password + "]";
	}

}
