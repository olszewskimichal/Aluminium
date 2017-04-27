package com.zespolowka.forms;

import java.util.Set;
import java.util.TreeSet;

import com.zespolowka.entity.createtest.ProgrammingLanguages;
import lombok.Data;

@Data
public class TaskForm {
	public static final int CLOSEDTASK = 0;
	public static final int OPENTASK = 1;
	public static final int PROGRAMMINGTASK = 2;
	public static final int SQLTASK = 3;

	private String question;

	private String answer;

	private int taskType;

	private Integer points = 1;

	private String preparations;

	private Set<String> languages = new TreeSet<>();

	private Set<ProgrammingTaskForm> programmingTaskForms;

	private Boolean caseSensitivity;

	private Boolean wrongReset = true;

	private Boolean countNotFull;

	public TaskForm() {
		programmingTaskForms = new TreeSet<>();
	}

	public TaskForm(int taskType) {
		this.taskType = taskType;
		programmingTaskForms = new TreeSet<>();
		if (taskType == 2) {
			programmingTaskForms.add(new ProgrammingTaskForm(ProgrammingLanguages.JAVA.toString()));
			programmingTaskForms.add(new ProgrammingTaskForm(ProgrammingLanguages.CPP.toString()));
			programmingTaskForms.add(new ProgrammingTaskForm(ProgrammingLanguages.PYTHON3.toString()));
		}
	}

	public void setLanguages(Set<String> languages) {

		if (languages == null) {
			this.languages = new TreeSet<>();
		}
		else this.languages = languages;
		Set<ProgrammingTaskForm> programmingTaskFormSet = new TreeSet<>();
		if (this.languages.contains(ProgrammingLanguages.JAVA.toString())) {
			programmingTaskFormSet.add(new ProgrammingTaskForm(ProgrammingLanguages.JAVA.toString(), true));
		}
		else {
			programmingTaskFormSet.add(new ProgrammingTaskForm(ProgrammingLanguages.JAVA.toString()));
		}
		if (this.languages.contains(ProgrammingLanguages.CPP.toString())) {
			programmingTaskFormSet.add(new ProgrammingTaskForm(ProgrammingLanguages.CPP.toString(), true));
		}
		else {
			programmingTaskFormSet.add(new ProgrammingTaskForm(ProgrammingLanguages.CPP.toString()));
		}
		if (this.languages.contains(ProgrammingLanguages.PYTHON3.toString())) {
			programmingTaskFormSet.add(new ProgrammingTaskForm(ProgrammingLanguages.PYTHON3.toString(), true));
		}
		else {
			programmingTaskFormSet.add(new ProgrammingTaskForm(ProgrammingLanguages.PYTHON3.toString()));
		}
		setProgrammingTaskForms(programmingTaskFormSet);
	}

	public Set<ProgrammingTaskForm> getProgrammingTaskForms() {
		if (programmingTaskForms.isEmpty()) {
			programmingTaskForms.add(new ProgrammingTaskForm(ProgrammingLanguages.JAVA.toString()));
			programmingTaskForms.add(new ProgrammingTaskForm(ProgrammingLanguages.CPP.toString()));
			programmingTaskForms.add(new ProgrammingTaskForm(ProgrammingLanguages.PYTHON3.toString()));
		}
		return programmingTaskForms;
	}

	public void setProgrammingTaskForms(Set<ProgrammingTaskForm> programmingTaskForms) {

		if (programmingTaskForms == null) {
			this.programmingTaskForms = new TreeSet<>();
		}
		else this.programmingTaskForms = programmingTaskForms;
	}

}
