package com.zespolowka.entity.solution.test;


public enum SolutionStatus {
	OPEN("Open"), DURING("During"), FINISHED("Finished");

	private String value;

	SolutionStatus(String displayName) {
		this.value = displayName;
	}

	public String getValue() {
		return value;
	}
}
