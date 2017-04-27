package com.zespolowka.builders;

import com.zespolowka.entity.user.User;

/**
 * Created by Pitek on 2015-12-03.
 */
public class UserBuilder {
	private int numberOfInstances = 1;
	private Long id;
	private String name;
	private String lastName;
	private String email = "test@o2.pl";
	private String passwordHash = "zaq1@WSX";

	public UserBuilder(String name, String lastName) {
		this.name = name;
		this.lastName = lastName;
	}

	public UserBuilder withEmail(String email) {
		this.email = email;
		return this;
	}

	public User build() {
		return new User(name, lastName, email, passwordHash);
	}
}
