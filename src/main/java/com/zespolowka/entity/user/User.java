package com.zespolowka.entity.user;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Created by Pitek on 2015-11-29.
 */
@Entity
@Table(name = "users")
@Data
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private String lastName;

	private String email;

	@JsonIgnore
	private String passwordHash;

	@JsonIgnore
	private boolean enabled;

	@JsonIgnore
	private int loginTries;

	@JsonIgnore
	private boolean accountNonLocked = true;

	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private Role role;

	public User() {
		this.enabled = false;
		this.loginTries = 3;
	}


	public User(String name, String lastName, String email, String passwordHash) {
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = Role.USER;
		this.enabled = false;
		this.loginTries = 3;
	}

}


