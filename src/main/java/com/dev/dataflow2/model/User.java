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

import com.dev.dataflow2.dto.UserDto;

/**
 * @author tonyr
 *
 */
@Entity
@Table
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int userid;

	@Column(name = "firstname")
	private String firstName;

	@Column(name = "lastname")
	private String lastName;

	@Column
	private String email;

	@Column
	private String salt;

	@Column
	private String password;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUserid() {
		return userid;
	}

	public User() {
		super();
	}

	public User(String firstName, String lastName, String email, String salt, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.salt = salt;
		this.password = password;
	}

	public User(UserDto userDto) {
		this.firstName = userDto.getFirstName();
		this.lastName = userDto.getLastName();
		this.password = userDto.getPassword();
		this.email = userDto.getEmail();
	}

}
