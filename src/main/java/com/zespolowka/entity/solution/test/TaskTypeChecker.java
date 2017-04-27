package com.zespolowka.entity.solution.test;


public class TaskTypeChecker {

	public static boolean isTaskClosedSolution(Object object) {
		return object instanceof TaskClosedSolution;
	}

	public static boolean isTaskOpenSolution(Object object) {
		return object instanceof TaskOpenSolution;
	}

	public static boolean isTaskProgrammingSolution(Object object) {
		return object instanceof TaskProgrammingSolution;
	}

	public static boolean isTaskSqlSolution(Object object) {
		return object instanceof TaskSqlSolution;
	}
}
