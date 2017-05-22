package com.zespolowka.service;


import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.zespolowka.entity.createtest.ProgrammingLanguages;
import com.zespolowka.forms.CreateTestForm;
import com.zespolowka.forms.ProgrammingTaskForm;
import com.zespolowka.forms.TaskForm;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestFormService {
	private static final String TEST_ATTRIBUTE_NAME = "testSession";
	private static final String EDIT_TEST_ATTRIBUTE_NAME = "editTestSession";
	private static final String EDIT_TEST_ID_ATTRIBUTE_NAME = "EditTestId";
	private final HttpSession httpSession;

	@Autowired
	public TestFormService(final HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	public CreateTestForm getTestFromSession() {
		log.info("Metoda - getTestFromSession");

		return (CreateTestForm) Optional.ofNullable(this.httpSession.getAttribute(TEST_ATTRIBUTE_NAME)).orElseGet(() -> {
			CreateTestForm testForm = new CreateTestForm();
			httpSession.setAttribute(EDIT_TEST_ATTRIBUTE_NAME, testForm);
			return testForm;
		});
	}

	public CreateTestForm getEditTestFromSession() {
		log.info("Metoda - getEditTestFromSession");
		return (CreateTestForm) Optional.ofNullable(this.httpSession.getAttribute(EDIT_TEST_ATTRIBUTE_NAME)).orElseGet(() -> {
			CreateTestForm testForm = new CreateTestForm();
			httpSession.setAttribute(EDIT_TEST_ATTRIBUTE_NAME, testForm);
			return testForm;
		});
	}


	public void updateTestFormInSession(final CreateTestForm createTestForm) {
		log.info("Metoda - updateTestFormInSession");
		this.httpSession.setAttribute(TEST_ATTRIBUTE_NAME, createTestForm);
	}

	public void updateEditTestFormInSession(final CreateTestForm createTestForm) {
		log.info("Metoda - updateEditTestFormInSession");
		this.httpSession.setAttribute(EDIT_TEST_ATTRIBUTE_NAME, createTestForm);
	}


	public void updateSelectedLanguagesInSession(final String selected) {
		log.info("Metoda - updateSelectedLanguagesInSession");
		this.httpSession.setAttribute("updateSelectedLanguagesInSession", selected);
	}

	public void addTaskFormToTestForm(final TaskForm taskForm) {
		log.info("Metoda - addTaskFormToTestForm");
		final CreateTestForm createTestForm = getTestFromSession();
		createTestForm.addTask(taskForm);
		updateTestFormInSession(createTestForm);
	}

	public void addTaskFormToEditTestForm(final TaskForm taskForm) {
		final CreateTestForm createTestForm = getEditTestFromSession();
		createTestForm.addTask(taskForm);
		updateTestFormInSession(createTestForm);
	}

	public Set<ProgrammingTaskForm> createProgrammingTaskSet(Set<ProgrammingTaskForm> programmingTaskFormSet, String[] languages, TaskForm taskForm) {
		Set<ProgrammingTaskForm> newProgrammingTaskFormSet = new TreeSet<>();

		for (ProgrammingLanguages prLanguage : ProgrammingLanguages.values()) {
			String language = prLanguage.toString();
			if (Arrays.asList(languages).indexOf(language) > -1) {
				if (taskForm.getLanguages().contains(language)) {
					programmingTaskFormSet.stream().filter(programmingTaskForm -> programmingTaskForm.getLanguage().equals(language)).forEach(programmingTaskForm -> {
						programmingTaskForm.setHidden(true);
						newProgrammingTaskFormSet.add(programmingTaskForm);
					});
				}
				else {
					newProgrammingTaskFormSet.add(new ProgrammingTaskForm(language, true));
				}
			}
			else {
				newProgrammingTaskFormSet.add(new ProgrammingTaskForm(language, false));
			}
		}

		return newProgrammingTaskFormSet;
	}

	public void setEditTestIdInSession(final Long id) {
		this.httpSession.setAttribute(EDIT_TEST_ID_ATTRIBUTE_NAME, id);
	}

	public Long getEditTestIdFromSession() {
		return (Long) this.httpSession.getAttribute(EDIT_TEST_ID_ATTRIBUTE_NAME);
	}

	public void removeEditTestIdInSession() {
		this.httpSession.removeAttribute(EDIT_TEST_ID_ATTRIBUTE_NAME);
	}

	public void removeEditTestFormInSession() {
		this.httpSession.removeAttribute(EDIT_TEST_ATTRIBUTE_NAME);
	}

}
