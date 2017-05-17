package com.zespolowka;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.TreeSet;

import com.zespolowka.forms.CreateTestForm;
import com.zespolowka.forms.ProgrammingTaskForm;
import com.zespolowka.forms.TaskForm;
import com.zespolowka.validators.CreateTestValidator;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@ActiveProfiles("test")
public class CreateTestFormValidationTest {
	private static final Logger logger = LoggerFactory.getLogger(CreateTestFormValidationTest.class);
	CreateTestForm createTestForm;
	CreateTestValidator createTestValidator;
	Errors errors;

	@Before
	public void Initialize() {
		createTestValidator = new CreateTestValidator();
		createTestForm = new CreateTestForm();
		createTestForm.setBeginDate("1999-11-11");
		createTestForm.setEndDate("2001-11-01");
		TaskForm taskForm = new TaskForm();
		taskForm.setQuestion("Pytanie 1");
		taskForm.setTaskType(TaskForm.CLOSEDTASK);
		taskForm.setPoints(BigDecimal.ONE);
		taskForm.setAnswer("aaaaaaaa" + "\n" + "<*>bbbbbbbbb");
		taskForm.setCountNotFull(true);
		createTestForm.getTasks().add(taskForm);

		taskForm = new TaskForm();
		ProgrammingTaskForm programmingTaskForm = new ProgrammingTaskForm("JAVA", true);
		taskForm.setQuestion("Pytanie 2");
		taskForm.setTaskType(TaskForm.PROGRAMMINGTASK);
		TreeSet<String> languages = new TreeSet<>();
		languages.add("java");
		taskForm.setLanguages(languages);
		programmingTaskForm.setTestCode("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		programmingTaskForm.setRestrictedList("bbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
		taskForm.getProgrammingTaskForms().add(programmingTaskForm);
		createTestForm.getTasks().add(taskForm);

		taskForm = new TaskForm();
		taskForm.setTaskType(TaskForm.SQLTASK);
		taskForm.setQuestion("Sql pytanie");
		taskForm.setAnswer("aaaaaaaaaaaa");
		taskForm.setPreparations("aaaaaaaaaaaaaaaaaa");
		createTestForm.getTasks().add(taskForm);

	}

	@Test
	public void checkValidatorWithCorrectDate() throws Exception {
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(0L, (long) errors.getErrorCount());

	}

	@Test
	public void checkValidatorWithOneIncorrectDate() throws Exception {
		createTestForm.setEndDate("01/01/1999");
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}

	@Test
	public void checkValidatorWithTwoIncorrectDate() throws Exception {
		createTestForm.setEndDate("01/01/1999");
		createTestForm.setBeginDate("");
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(2L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}

	@Test
	public void checkValidatorWithBeginDateLessThenEndDate() throws Exception {
		createTestForm.setEndDate("1999-11-11");
		createTestForm.setBeginDate("2001-11-01");
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}

	@Test
	public void checkValidatorWithBeginDateEqualEndDate() throws Exception {
		createTestForm.setEndDate("1999-11-11");
		createTestForm.setBeginDate("1999-11-11");
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}

	@Test
	public void checkValidatorWithTaskWithoutQuestion() throws Exception {
		createTestForm.getTasks().get(0).setQuestion("");
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}

	@Test
	public void checkValidatorWithTaskWithQuestionLengthLessThen5() throws Exception {
		createTestForm.getTasks().get(0).setQuestion("aaa");
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}

	@Test
	public void checkValidatorWithTaskWithoutAnswer() throws Exception {
		createTestForm.getTasks().get(0).setAnswer("aa");
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}

	@Test
	public void checkValidatorWithTaskWithoutPoints() throws Exception {
		createTestForm.getTasks().get(0).setPoints(null);
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}

	@Test
	public void checkValidatorWithClosedTaskWithoutCountingPointOption() throws Exception {
		createTestForm.getTasks().get(0).setCountNotFull(false);
		createTestForm.getTasks().get(0).setWrongReset(false);
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}

	@Test
	public void checkValidatorWithClosedTaskWithoutCorrectAnswer() throws Exception {
		createTestForm.getTasks().get(0).setAnswer("sól\n" + "pieprz");
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}

	@Test
	public void checkValidatorWithProgrammingTaskWithoutChoosenLanguage() throws Exception {
		createTestForm.getTasks().get(1).setLanguages(new TreeSet<>());
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}


	@Test
	public void checkValidatorWithProgrammingTaskWithProgramingDetailTaskTestCodeNull() throws Exception {
		ProgrammingTaskForm programmingTaskForm = new ProgrammingTaskForm("java", true);
		programmingTaskForm.setRestrictedList("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		programmingTaskForm.setTestCode("");
		createTestForm.getTasks().get(1).getProgrammingTaskForms().add(programmingTaskForm);
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());

	}

	@Test
	public void checkValidatorWithSqlTaskWithoutPreparations() throws Exception {
		createTestForm.getTasks().get(2).setPreparations("");
		errors = new BindException(createTestForm, "createTestForm");
		ValidationUtils.invokeValidator(createTestValidator, createTestForm, errors);
		assertEquals(1L, (long) errors.getErrorCount());
		logger.info(errors.getAllErrors().toString());
	}

}