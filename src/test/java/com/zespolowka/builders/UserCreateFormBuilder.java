package com.zespolowka.builders;

import com.zespolowka.forms.UserCreateForm;

/**
 * Created by Pitek on 2015-12-03.
 */
public class UserCreateFormBuilder {
	private int numberOfInstances = 1;
	private String name;
	private String lastName;
	private String email;
	private String password = "12345678";
	private String confirmPassword = "12345678";

	public UserCreateFormBuilder(String name, String lastName) {
		this.name = name;
		this.lastName = lastName;
	}

	public UserCreateFormBuilder withEmail(String email) {
		this.email = email;
		return this;
	}

	public UserCreateForm build() {
		return new UserCreateForm(name, lastName, email, password, confirmPassword);
	}
}
