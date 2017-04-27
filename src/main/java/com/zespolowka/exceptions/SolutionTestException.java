package com.zespolowka.exceptions;

public class SolutionTestException extends RuntimeException {
	public SolutionTestException(String error) {
		super(String.format("Błedn podczas rozwiazywania testu %s", error));
	}

}
