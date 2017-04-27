package com.zespolowka.exceptions;

public class LoginFailedException extends RuntimeException {
	public LoginFailedException(String username) {
		super(String.format("Błedne logowanie uzytkownika %s", username));
	}

}
