package com.zespolowka.service;

import static com.zespolowka.entity.createtest.TaskClosed.COUNT_NOT_FULL;
import static com.zespolowka.entity.createtest.TaskClosed.WRONG_RESET;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.zespolowka.entity.createtest.ProgrammingLanguages;
import com.zespolowka.entity.createtest.Task;
import com.zespolowka.entity.createtest.TaskClosed;
import com.zespolowka.entity.createtest.TaskOpen;
import com.zespolowka.entity.createtest.TaskProgramming;
import com.zespolowka.entity.createtest.TaskProgrammingDetail;
import com.zespolowka.entity.createtest.TaskSql;
import com.zespolowka.entity.createtest.Test;
import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.forms.CreateTestForm;
import com.zespolowka.forms.NewMessageForm;
import com.zespolowka.forms.ProgrammingTaskForm;
import com.zespolowka.forms.TaskForm;
import com.zespolowka.repository.SolutionTestRepository;
import com.zespolowka.repository.TestRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TestService {
	private final TestRepository testRepository;

	private final SolutionTestRepository solutionTestRepository;

	private final NotificationService notificationService;

	private final UserService userService;

	@Autowired
	public TestService(final TestRepository testRepository, SolutionTestRepository solutionTestRepository, NotificationService notificationService, UserService userService) {
		this.testRepository = testRepository;
		this.solutionTestRepository = solutionTestRepository;
		this.notificationService = notificationService;
		this.userService = userService;
	}


	public Test getTestById(final long id) {
		return testRepository.findTestById(id);
	}

	public Collection<Test> getAllTests() {
		return testRepository.findAll();
	}

	public Test create(final CreateTestForm form) {
		Test test = new Test();
		try {
			test = createTestFromForm(form);
			ResourceBundle messages = ResourceBundle.getBundle("messages", LocaleContextHolder.getLocale());
			NewMessageForm newMessageForm = new NewMessageForm();
			newMessageForm.setReceivers(Role.USER.name());
			newMessageForm.setTopic(messages.getString("test_created.topic") + " " + test.getName());
			newMessageForm.setMessage(messages.getString("test_created.message"));
			User system = userService.getUserById(1L).orElseThrow(() -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", 1)));
			log.info("SYS:{}", system);
			newMessageForm.setSender(system);
			notificationService.sendMessage(newMessageForm);
		}
		catch (Exception e) {
			log.info(e.getMessage(), e);
			log.info(test.toString());
			log.info(form.toString());
		}
		return testRepository.save(test);
	}

	public Test update(CreateTestForm form, Long id) {
		Test test = new Test();
		Test test1 = new Test();
		try {
			test = createTestFromForm(form);
			test1 = testRepository.findTestById(id);
			test1.setTimePerAttempt(test.getTimePerAttempt());
			test1.setPassHash(test.getPassHash());
			test1.setTasks(test.getTasks());
			test1.setAttempts(test.getAttempts());
			test1.setBeginDate(test.getBeginDate());
			test1.setEndDate(test.getEndDate());
			test1.setMaxPoints(test.getMaxPoints());
			test1.setName(test.getName());
			test1.setMessageFAQ(test.getMessageFAQ());
		}
		catch (Exception e) {
			log.info(e.getMessage(), e);
			log.info(test.toString());
			log.info(test1.toString());
			log.info(form.toString());
			log.info(id.toString());
		}
		return testRepository.save(test1);
	}

	@Transactional
	public void delete(Long id) {
		solutionTestRepository.deleteSolutionTestsByTest(testRepository.findTestById(id));
		testRepository.delete(id);
	}

	@Transactional
	public Test update(Test test) {
		return testRepository.save(test);
	}

	public CreateTestForm createForm(Test test) {
		CreateTestForm createTestForm = new CreateTestForm();
		try {
			createTestForm.setName(test.getName());
			createTestForm.setPassword(test.getPassHash());
			createTestForm.setBeginDate(test.getBeginDate().toString());
			createTestForm.setEndDate(test.getEndDate().toString());
			createTestForm.setAttempts(test.getAttempts().intValue());
			createTestForm.setTimePerAttempt(test.getTimePerAttempt());
			createTestForm.setMessageFAQ(test.getMessageFAQ());
			List<TaskForm> taskForms = new ArrayList<>();
			for (Task task : test.getTasks()) {
				TaskForm taskForm = new TaskForm();
				if (task instanceof TaskClosed) {
					taskForm.setTaskType(TaskForm.CLOSEDTASK);
					taskForm.setQuestion(task.getQuestion());
					taskForm.setPoints(task.getMaxPoints().intValue());
					if (((TaskClosed) task).getCountingType() == WRONG_RESET) {
						taskForm.setWrongReset(true);
						taskForm.setCountNotFull(false);
					}
					else {
						taskForm.setCountNotFull(true);
						taskForm.setWrongReset(false);
					}
					StringBuilder answer = new StringBuilder();
					for (Map.Entry<String, Boolean> stringBooleanEntry : ((TaskClosed) task).getAnswers().entrySet()) {
						if (stringBooleanEntry.getValue()) {
							answer.append("<*>").append(stringBooleanEntry.getKey()).append("\n");
						}
						else
							answer.append(stringBooleanEntry.getKey()).append("\n");
					}
					taskForm.setAnswer((answer.toString()));
				}
				else if (task instanceof TaskOpen) {
					taskForm.setTaskType(TaskForm.OPENTASK);
					taskForm.setQuestion(task.getQuestion());
					taskForm.setAnswer(((TaskOpen) task).getAnswer());
					taskForm.setPoints(task.getMaxPoints().intValue());
					taskForm.setCaseSensitivity(((TaskOpen) task).getCaseSens());

				}
				else if (task instanceof TaskProgramming) {
					taskForm.setTaskType(TaskForm.PROGRAMMINGTASK);
					taskForm.setQuestion(task.getQuestion());
					taskForm.setPoints(task.getMaxPoints().intValue());
					Set<TaskProgrammingDetail> taskProgrammingDetailSet = ((TaskProgramming) task).getProgrammingDetailSet();
					Set<ProgrammingTaskForm> programmingTaskFormSet = new TreeSet<>();
					Set<String> languages = new TreeSet<>();
					for (TaskProgrammingDetail taskProgrammingDetail : taskProgrammingDetailSet) {
						ProgrammingTaskForm programmingTaskForm = new ProgrammingTaskForm(taskProgrammingDetail.getLanguage().toString(), true);
						programmingTaskForm.setTestCode(taskProgrammingDetail.getTestCode());
						programmingTaskForm.setTestClassName(taskProgrammingDetail.getTestClassName());
						programmingTaskForm.setSolutionClassName(taskProgrammingDetail.getSolutionClassName());
						programmingTaskForm.setRestrictedList(taskProgrammingDetail.getRestrictedList());
						languages.add(taskProgrammingDetail.getLanguage().toString());
						programmingTaskFormSet.add(programmingTaskForm);
					}
					for (ProgrammingLanguages lang : ProgrammingLanguages.values()) {
						if (!languages.contains(lang.name())) {
							ProgrammingTaskForm programmingTaskForm = new ProgrammingTaskForm(lang.name(), false);
							programmingTaskFormSet.add(programmingTaskForm);
						}
					}
					taskForm.setLanguages(languages);
					taskForm.setProgrammingTaskForms(programmingTaskFormSet);
				}
				else if (task instanceof TaskSql) {
					taskForm.setTaskType(TaskForm.SQLTASK);
					taskForm.setQuestion(task.getQuestion());
					taskForm.setPoints(task.getMaxPoints().intValue());
					taskForm.setPreparations(((TaskSql) task).getPreparations());
					taskForm.setAnswer(((TaskSql) task).getSqlAnswer());
				}
				taskForms.add(taskForm);
			}
			createTestForm.setTasks(taskForms);
		}
		catch (Exception e) {
			log.info(e.getMessage(), e);
			log.info(test.toString());
			log.info(createTestForm.toString());
		}
		return createTestForm;
	}

	Test createTestFromForm(CreateTestForm form) {
		Test test = new Test();
		try {
			List<TaskForm> taskFormList = form.getTasks();
			test.setAttempts(Long.valueOf(form.getAttempts()));
			test.setPassHash(form.getPassword());
			test.setTimePerAttempt(form.getTimePerAttempt());
			test.setBeginDate(LocalDate.parse(form.getBeginDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			test.setEndDate(LocalDate.parse(form.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			test.setName(form.getName());
			test.setMessageFAQ(form.getMessageFAQ());
			for (TaskForm taskForm : taskFormList) {
				switch (taskForm.getTaskType()) {
					case 0: {
						TaskClosed taskClosed = new TaskClosed(taskForm.getQuestion(), (float) taskForm.getPoints());
						String answerList = taskForm.getAnswer();
						String[] answers = answerList.split("[\\r\\n]+");
						for (String answer : answers) {
							if (answer.startsWith("<*>")) {
								taskClosed.getAnswers().put(answer.substring(3, answer.length()), true);
							}
							else {
								taskClosed.getAnswers().put(answer, false);
							}
						}
						if (taskForm.getWrongReset()) {
							taskClosed.setCountingType(WRONG_RESET);
						}
						else {
							taskClosed.setCountingType(COUNT_NOT_FULL);
						}
						test.addTaskToTest(taskClosed);
						break;
					}
					case 1: {
						TaskOpen taskOpen = new TaskOpen(taskForm.getQuestion(), (float) taskForm.getPoints());
						taskOpen.setAnswer(taskForm.getAnswer());
						taskOpen.setCaseSens(taskForm.getCaseSensitivity());
						test.addTaskToTest(taskOpen);
						break;
					}
					case 2: {
						TaskProgramming taskProgramming = new TaskProgramming(taskForm.getQuestion(), (float) taskForm.getPoints());
						Set<ProgrammingTaskForm> programmingTaskForms = taskForm.getProgrammingTaskForms();
						programmingTaskForms.stream().filter(ProgrammingTaskForm::getHidden).forEach(programmingTaskForm -> {
							TaskProgrammingDetail taskProgrammingDetail = new TaskProgrammingDetail();
							taskProgrammingDetail.setTestCode(programmingTaskForm.getTestCode());
							taskProgrammingDetail.setRestrictedList(programmingTaskForm.getRestrictedList());
							taskProgrammingDetail.setLanguage(ProgrammingLanguages.valueOf(programmingTaskForm.getLanguage()));
							taskProgrammingDetail.setSolutionClassName(programmingTaskForm.getSolutionClassName());
							taskProgrammingDetail.setTestClassName(programmingTaskForm.getTestClassName());
							taskProgramming.addTaskProgrammingDetail(taskProgrammingDetail);
						});
						test.addTaskToTest(taskProgramming);
						break;
					}
					case 3: {
						TaskSql taskSql = new TaskSql(taskForm.getQuestion(), (float) taskForm.getPoints());
						taskSql.setPreparations(taskForm.getPreparations());
						taskSql.setSqlAnswer(taskForm.getAnswer());
						test.addTaskToTest(taskSql);

						break;
					}
				}
			}
		}
		catch (Exception e) {
			log.info(e.getMessage(), e);
			log.info(test.toString());
			log.info(form.toString());
		}
		return test;
	}

	public Collection<Test> getTestByEndDateBefore(LocalDate date) {
		return testRepository.findByEndDateBefore(date);
	}

	public Collection<Test> getTestByEndDateAfter(LocalDate date) {
		return testRepository.findByEndDateAfter(date);
	}

	public void createPDF(File file, String title, String[] header, String[][] body) {
		try {
			log.info("createPDF");
			Document documento = new Document();
			float[] columnWidths = new float[] {15.0f, 30.0f, 30.0f, 15.0f, 40.0f};

			if (file.createNewFile()) {
				FileOutputStream fop = new FileOutputStream(file);
				PdfWriter.getInstance(documento, fop);
				documento.open();
				BaseFont helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);

				Font fontHeader = new Font(helvetica, 12.0F, Font.BOLD);
				Font fontBody = new Font(helvetica, 12.0F, Font.NORMAL);
				documento.add(new Paragraph(title, new Font(helvetica, 20.0F, Font.BOLD, BaseColor.BLACK)));
				//Table for header
				PdfPTable cabetabla = new PdfPTable(header.length);
				for (String aHeader : header) {
					Phrase frase = new Phrase(aHeader, fontHeader);
					PdfPCell cell = new PdfPCell(frase);
					cell.setBackgroundColor(new BaseColor(Color.lightGray.getRGB()));
					cabetabla.addCell(cell);
				}

				cabetabla.setWidths(columnWidths);
				documento.add(cabetabla);
				//Table for body
				PdfPTable tabla = new PdfPTable(header.length);
				for (String[] aBody : body) {
					for (String anABody : aBody) {
						tabla.addCell(new Phrase(anABody, fontBody));
					}
				}
				tabla.setWidths(columnWidths);
				documento.add(tabla);

				documento.close();
				fop.flush();
				fop.close();
			}
		}
		catch (IOException | DocumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}

	}
}
