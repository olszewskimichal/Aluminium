package com.zespolowka.validators;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

import com.zespolowka.forms.CreateTestForm;
import com.zespolowka.forms.ProgrammingTaskForm;
import com.zespolowka.forms.TaskForm;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Slf4j
public class CreateTestValidator implements Validator {
	private Boolean questionNull = false;
	private Boolean invalidBeginDate = false;
	private Boolean invalidEndDate = false;
	private Boolean beginDateAfterEndDate = false;
	private Boolean questionWithoutAnswer = false;
	private Boolean closedTaskWithoutCorrectAnswer = false;
	private Boolean closedTaskWithoutCountingPointOption = false;
	private Boolean taskWithoutPoints = false;
	private Boolean programmingTaskWithoutChoosenLanguage = false;
	private Boolean programingDetailTaskTestCodeNull = false;
	private Boolean sqlTaskWithoutPreparations = false;
	private Boolean closedTaskWithWrongCountOfAnswers = false;
	private int invalidTask;

	public CreateTestValidator() {
		questionNull = false;
		invalidBeginDate = false;
		invalidEndDate = false;
		beginDateAfterEndDate = false;
		questionWithoutAnswer = false;
		closedTaskWithoutCorrectAnswer = false;
		closedTaskWithoutCountingPointOption = false;
		taskWithoutPoints = false;
		programmingTaskWithoutChoosenLanguage = false;
		programingDetailTaskTestCodeNull = false;
		sqlTaskWithoutPreparations = false;
		closedTaskWithWrongCountOfAnswers = false;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(CreateTestForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CreateTestForm createTestForm = (CreateTestForm) target;
		log.info("Walidacja testu {}", createTestForm);


		final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		if (createTestForm.getPassword() == null)
			createTestForm.setPassword("");

		invalidBeginDate = false;
		invalidEndDate = false;
		beginDateAfterEndDate = false;

		try {
			LocalDate.parse(createTestForm.getBeginDate(), dateTimeFormatter);
		}
		catch (DateTimeParseException ignored) {
			invalidBeginDate = true;
		}
		try {
			LocalDate.parse(createTestForm.getEndDate(), dateTimeFormatter);
		}
		catch (DateTimeParseException ignored) {
			invalidEndDate = true;
		}

		if (!invalidBeginDate && !invalidEndDate) {
			if ((LocalDate.parse(createTestForm.getEndDate(), dateTimeFormatter).isBefore(LocalDate.parse(createTestForm.getBeginDate(), dateTimeFormatter))) || (LocalDate.parse(createTestForm.getEndDate(), dateTimeFormatter).isEqual(LocalDate.parse(createTestForm.getBeginDate(), dateTimeFormatter)))) {
				beginDateAfterEndDate = true;
			}
		}

		List<TaskForm> taskForms = createTestForm.getTasks();
		questionNull = false;
		questionWithoutAnswer = false;
		closedTaskWithoutCorrectAnswer = false;
		closedTaskWithoutCountingPointOption = false;
		taskWithoutPoints = false;
		programmingTaskWithoutChoosenLanguage = false;
		programingDetailTaskTestCodeNull = false;
		sqlTaskWithoutPreparations = false;
		if (!createTestForm.getTasks().isEmpty()) {
			log.info("{}{}", closedTaskWithoutCorrectAnswer, questionNull);
			for (TaskForm taskForm : taskForms) {
				if (!questionNull && (taskForm.getQuestion() == null || taskForm.getQuestion().length() < 5)) {
					questionNull = true;
					invalidTask = taskForms.indexOf(taskForm);
				}
				if (!taskWithoutPoints && (taskForm.getPoints() == null || taskForm.getPoints().compareTo(BigDecimal.ONE) < 0)) {
					taskWithoutPoints = true;
					invalidTask = taskForms.indexOf(taskForm);
				}
				if (taskForm.getTaskType() == TaskForm.CLOSEDTASK) {
					if (!questionWithoutAnswer && (taskForm.getAnswer() == null || taskForm.getAnswer().length() < 3)) {
						questionWithoutAnswer = true;
						invalidTask = taskForms.indexOf(taskForm);
					}
					if (!closedTaskWithoutCountingPointOption && ((!taskForm.getCountNotFull() || taskForm.getCountNotFull() == null) && (!taskForm.getWrongReset() || taskForm.getWrongReset() == null))) {
						closedTaskWithoutCountingPointOption = true;
						invalidTask = taskForms.indexOf(taskForm);
					}
					if (!closedTaskWithoutCorrectAnswer && !questionWithoutAnswer) {
						String[] answers = taskForm.getAnswer().split("[\\r\\n]+");
						if (answers.length < 2 && answers.length > 10)
							closedTaskWithWrongCountOfAnswers = true;
						Boolean haveCorrectAnswer = false;
						for (String answer : answers) {
							if (answer.startsWith("<*>")) {
								haveCorrectAnswer = true;
								break;
							}
						}
						if (!haveCorrectAnswer) {
							closedTaskWithoutCorrectAnswer = true;
							invalidTask = taskForms.indexOf(taskForm);
						}
					}
				}
				if (taskForm.getTaskType() == TaskForm.PROGRAMMINGTASK) {
					if (!programmingTaskWithoutChoosenLanguage && (taskForm.getLanguages().isEmpty() || taskForm.getLanguages() == null)) {
						programmingTaskWithoutChoosenLanguage = true;
						invalidTask = taskForms.indexOf(taskForm);
					}
					if (!programmingTaskWithoutChoosenLanguage) {
						Set<ProgrammingTaskForm> programmingTaskFormSet = taskForm.getProgrammingTaskForms();
						programmingTaskFormSet.stream().filter(programmingTaskForm -> programmingTaskForm.getHidden() && !programingDetailTaskTestCodeNull && (programmingTaskForm.getTestCode() == null || programmingTaskForm.getTestCode().length() < 10)).forEach(programmingTaskForm -> {
							programingDetailTaskTestCodeNull = true;
							invalidTask = taskForms.indexOf(taskForm);
						});
					}
				}
				if (taskForm.getTaskType() == TaskForm.SQLTASK) {
					if (!sqlTaskWithoutPreparations && (taskForm.getPreparations().length() < 6 || taskForm.getPreparations() == null)) {
						sqlTaskWithoutPreparations = true;
						invalidTask = taskForms.indexOf(taskForm);
					}
				}
			}
		}
		if (invalidBeginDate)
			errors.rejectValue("beginDate", "Error.createTestForm.beginDate");
		if (invalidEndDate)
			errors.rejectValue("endDate", "Error.createTestForm.endDate");
		if (beginDateAfterEndDate)
			errors.rejectValue("endDate", "Error.createTestForm.beginDateAfterEndDate");
		if (questionNull)
			errors.rejectValue("tasks[" + invalidTask + "].question", "Error.createTestForm.question");
		if (questionWithoutAnswer)
			errors.rejectValue("tasks[" + invalidTask + "].answer", "Error.createTestForm.answer");
		if (taskWithoutPoints)
			errors.rejectValue("tasks[" + invalidTask + "].points", "Error.createTestForm.points");
		if (closedTaskWithoutCountingPointOption)
			errors.rejectValue("tasks[" + invalidTask + "].wrongReset", "Error.createTestForm.countingPoint");
		if (closedTaskWithoutCorrectAnswer)
			errors.rejectValue("tasks[" + invalidTask + "].answer", "Error.createTestForm.wrongAnswerCount");
		if (closedTaskWithWrongCountOfAnswers)
			errors.rejectValue("tasks[" + invalidTask + "].answer", "Error.createTestForm.correctAnswer");
		if (programmingTaskWithoutChoosenLanguage)
			errors.rejectValue("tasks[" + invalidTask + "].languages", "Error.createTestForm.Languages");
		if (programingDetailTaskTestCodeNull)
			errors.rejectValue("tasks[" + invalidTask + "].languages", "Error.createTestForm.testCode");
		if (sqlTaskWithoutPreparations)
			errors.rejectValue("tasks[" + invalidTask + "].preparations", "Error.createTestForm.preparations");

		log.info(toString());
	}

	@Override
	public String toString() {
		return "CreateTestValidator{" + "questionNull=" + questionNull + ", invalidBeginDate=" + invalidBeginDate + ", invalidEndDate=" + invalidEndDate + ", beginDateAfterEndDate=" + beginDateAfterEndDate + ", questionWithoutAnswer=" + questionWithoutAnswer + ", closedTaskWithoutCorrectAnswer=" + closedTaskWithoutCorrectAnswer + ", closedTaskWithoutCountingPointOption=" + closedTaskWithoutCountingPointOption + ", taskWithoutPoints=" + taskWithoutPoints + ", programmingTaskWithoutChoosenLanguage=" + programmingTaskWithoutChoosenLanguage + ", programingDetailTaskTestCodeNull=" + programingDetailTaskTestCodeNull + ", invalidTask=" + invalidTask + '}';
	}
}
