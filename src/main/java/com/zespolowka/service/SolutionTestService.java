package com.zespolowka.service;


import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import com.zespolowka.entity.createtest.ProgrammingLanguages;
import com.zespolowka.entity.createtest.Task;
import com.zespolowka.entity.createtest.TaskClosed;
import com.zespolowka.entity.createtest.TaskOpen;
import com.zespolowka.entity.createtest.TaskProgramming;
import com.zespolowka.entity.createtest.TaskProgrammingDetail;
import com.zespolowka.entity.createtest.TaskSql;
import com.zespolowka.entity.createtest.Test;
import com.zespolowka.entity.solution.test.CompilationError;
import com.zespolowka.entity.solution.test.CompilationErrorTypes;
import com.zespolowka.entity.solution.test.SolutionStatus;
import com.zespolowka.entity.solution.test.SolutionTest;
import com.zespolowka.entity.solution.test.TaskClosedSolution;
import com.zespolowka.entity.solution.test.TaskOpenSolution;
import com.zespolowka.entity.solution.test.TaskProgrammingSolution;
import com.zespolowka.entity.solution.test.TaskSolution;
import com.zespolowka.entity.solution.test.TaskSqlSolution;
import com.zespolowka.entity.solution.test.config.SolutionConfig;
import com.zespolowka.entity.user.User;
import com.zespolowka.exceptions.SolutionTestException;
import com.zespolowka.forms.NewMessageForm;
import com.zespolowka.forms.SolutionTaskForm;
import com.zespolowka.forms.SolutionTestForm;
import com.zespolowka.repository.SolutionTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SolutionTestService {
	private static final String OUTPUT = "output.json";
	private static final String CONFIG = "config.json";
	private static final String INTEGER_LIST = "integerList";

	private final SolutionTestRepository solutionTestRepository;
	private final HttpSession httpSession;
	private final NotificationService notificationService;
	private final Environment environment;
	private final UserService userService;

	private int taskNo = 0;

	private String dir = "/var/www/Aluminium/Team_Programming_Rewritten/";

	private String resultDir = "/tmp/";

	@Autowired
	public SolutionTestService(SolutionTestRepository solutionTestRepository, HttpSession httpSession, NotificationService notificationService, Environment environment, UserService userService) {
		this.solutionTestRepository = solutionTestRepository;
		this.httpSession = httpSession;
		this.notificationService = notificationService;
		this.environment = environment;
		this.userService = userService;
	}

	public Integer countSolutionTestsByTestAndSolutionStatus(Test test, SolutionStatus solutionStatus) {
		return solutionTestRepository.countSolutionTestsByTestAndSolutionStatus(test, solutionStatus);
	}

	public List<SolutionTest> getSolutionsWithTheBestResult(User user) {
		return solutionTestRepository.getSolutionsWithTheBestResult(user, SolutionStatus.FINISHED);
	}

	public Integer countSolutionTestsByUserAndTest(User user, Test test) {
		return solutionTestRepository.countSolutionTestsByUserAndTestAndSolutionStatus(user, test, SolutionStatus.FINISHED);
	}

	public SolutionTest getSolutionTestById(long id) {
		return solutionTestRepository.findSolutionTestById(id);
	}


	public SolutionTest create(SolutionTest solutionTest, SolutionStatus solutionStatus) {

		taskNo = 0;
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m:s");
		LocalDateTime dateTime = LocalDateTime.now();
		solutionTest.setEndSolution(LocalDateTime.parse(dateTime.getYear() + "/" + dateTime.getMonthValue() + '/' + dateTime.getDayOfMonth() + ' ' + dateTime.getHour() + ':' + dateTime.getMinute() + ':' + dateTime.getSecond(), dateTimeFormatter));
		solutionTest.setSolutionStatus(solutionStatus);
		if (solutionTest.getSolutionStatus() == SolutionStatus.FINISHED) {
			ResourceBundle messages = ResourceBundle.getBundle("messages", LocaleContextHolder.getLocale());
			NewMessageForm newMessageForm = new NewMessageForm();
			newMessageForm.setReceivers(solutionTest.getUser().getEmail());
			newMessageForm.setTopic(messages.getString("results.topic") + " " + solutionTest.getTest().getName());
			newMessageForm.setMessage(messages.getString("results.message") + " " + solutionTest.getPoints() + " / " + solutionTest.getTest().getMaxPoints());
			User system = userService.getUserById(1L).orElseThrow(() -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", 1)));
			newMessageForm.setSender(system);
			notificationService.sendMessage(newMessageForm);
		}
		return solutionTestRepository.saveAndFlush(solutionTest);
	}

	public SolutionTestForm createForm(Test test, User user) {
		SolutionTestForm solutionTestForm = new SolutionTestForm();
		SolutionTest solutionTest;
		Optional<SolutionTest> solutionTest2 = findSolutionTestByTestAndUserAndSolutionStatus(test, user, SolutionStatus.OPEN);
		if (!solutionTest2.isPresent()) {
			solutionTest = new SolutionTest(test, user);
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m:s");
			LocalDateTime dateTime = LocalDateTime.now();
			solutionTest.setBeginSolution(LocalDateTime.parse(dateTime.getYear() + "/" + dateTime.getMonthValue() + '/' + dateTime.getDayOfMonth() + ' ' + dateTime.getHour() + ':' + dateTime.getMinute() + ':' + dateTime.getSecond(), dateTimeFormatter));
			solutionTest.setAttempt(countSolutionTestsByUserAndTest(user, test) + 1);
			solutionTest.setSolutionStatus(SolutionStatus.OPEN);
			solutionTestRepository.saveAndFlush(solutionTest);
			solutionTestRepository.flush();
		}
		else solutionTest = solutionTest2.get();
		this.taskNo = 0;
		solutionTestForm.setName(test.getName());
		solutionTestForm.setSolutionId(solutionTest.getId());
		List<SolutionTaskForm> solutionTaskFormList = new ArrayList<>();
		List<Task> tasks = test.getTasks();
		Collections.shuffle(tasks);
		test.setTasks(tasks);
		solutionTest.setTest(test);

		List<Long> integerList = new ArrayList<>();
		for (Task task : test.getTasks()) {
			integerList.add(task.getId());
			if (task instanceof TaskClosed) {
				solutionTaskFormList.add(new SolutionTaskForm(task, SolutionTaskForm.CLOSEDTASK));
			}
			else if (task instanceof TaskOpen) {
				solutionTaskFormList.add(new SolutionTaskForm(task, SolutionTaskForm.OPENTASK));
			}
			else if (task instanceof TaskProgramming) {
				solutionTaskFormList.add(new SolutionTaskForm(task, SolutionTaskForm.PROGRAMMINGTASK));
			}
			else if (task instanceof TaskSql) {
				solutionTaskFormList.add(new SolutionTaskForm(task, SolutionTaskForm.SQLTASK));
			}
		}
		httpSession.setAttribute(INTEGER_LIST, integerList);
		solutionTestRepository.saveAndFlush(solutionTest);
		solutionTestForm.setTasks(solutionTaskFormList);
		this.taskNo = 0;
		return solutionTestForm;
	}

	public void addTaskSolutionToTest(SolutionTest solutionTest, TaskSolution taskSolution) throws IOException, ParseException, InterruptedException {
		List<Long> integerList = (List<Long>) httpSession.getAttribute(INTEGER_LIST);
		if (environment.getActiveProfiles().length > 0 && "prod".equals(environment.getActiveProfiles()[0])) {
			Long minimumId = Collections.min(integerList);
			solutionTest.getTest().getTasks().get(integerList.get(taskNo++).intValue() - minimumId.intValue()).addTaskSolution(taskSolution);
		}
		else
			solutionTest.getTest().getTasks().get(taskNo++).addTaskSolution(taskSolution);
		if (taskSolution instanceof TaskClosedSolution) {
			TaskClosedSolution taskSol = (TaskClosedSolution) taskSolution;
			TaskClosed taskClo = (TaskClosed) taskSol.getTask();
			Map<String, Boolean> userAnswers = taskSol.getAnswers();
			Map<String, Boolean> correctAnswers = taskClo.getAnswers();
			if (taskClo.getCountingType() == TaskClosed.WRONG_RESET) {
				Boolean theSame = true;
				for (Map.Entry<String, Boolean> stringBooleanEntry : userAnswers.entrySet()) {
					if (stringBooleanEntry.getValue() == null) {
						userAnswers.put(stringBooleanEntry.getKey(), false);
					}
					if ((stringBooleanEntry.getValue() != null && stringBooleanEntry.getValue()) && (!correctAnswers.get(stringBooleanEntry.getKey())) || ((stringBooleanEntry.getValue() == null || !stringBooleanEntry.getValue()) && (correctAnswers.get(stringBooleanEntry.getKey())))) {
						theSame = false;
					}
				}
				if (theSame) {
					taskSol.setPoints(taskClo.getMaxPoints());
				}
				else taskSol.setPoints(BigDecimal.ZERO);
			}
			else {
				BigDecimal pointsDivide = BigDecimal.ZERO;
				BigDecimal noCorrectAnswers = BigDecimal.ZERO;
				Boolean chooseIncorect = false;
				for (Map.Entry<String, Boolean> stringBooleanEntry : userAnswers.entrySet()) {
					if (stringBooleanEntry.getValue() == null)
						userAnswers.put(stringBooleanEntry.getKey(), false);
					if (correctAnswers.get(stringBooleanEntry.getKey()))
						pointsDivide = pointsDivide.add(BigDecimal.ONE);
					if (stringBooleanEntry.getValue() && !correctAnswers.get(stringBooleanEntry.getKey())) {
						chooseIncorect = true;
						break;
					}
					else if (stringBooleanEntry.getValue().equals(correctAnswers.get(stringBooleanEntry.getKey())) && correctAnswers.get(stringBooleanEntry.getKey()))
						noCorrectAnswers = noCorrectAnswers.add(BigDecimal.ONE);
				}
				if (chooseIncorect || noCorrectAnswers.compareTo(BigDecimal.ONE) < 0) {
					taskSol.setPoints(BigDecimal.ZERO);
				}
				else {
					taskSol.setPoints(taskClo.getMaxPoints().divide(pointsDivide.divide(noCorrectAnswers, RoundingMode.HALF_UP), BigDecimal.ROUND_HALF_UP));
				}
			}
			solutionTest.setPoints(solutionTest.getPoints().add(taskSol.getPoints()));
			solutionTest.addSolutionTask(taskSol);
		}
		if (taskSolution instanceof TaskOpenSolution) {
			TaskOpenSolution taskSol = (TaskOpenSolution) taskSolution;
			TaskOpen taskOp = (TaskOpen) taskSol.getTask();
			if (!taskOp.getCaseSens()) {
				if (taskSol.getAnswer().equalsIgnoreCase(taskOp.getAnswer())) {
					solutionTest.setPoints(solutionTest.getPoints().add(taskOp.getMaxPoints()));
					taskSol.setPoints(taskOp.getMaxPoints());
				}
				else taskSol.setPoints(BigDecimal.ZERO);
			}
			else {
				if (taskSol.getAnswer().equals(taskOp.getAnswer())) {
					solutionTest.setPoints(solutionTest.getPoints().add(taskOp.getMaxPoints()));
					taskSol.setPoints(taskOp.getMaxPoints());
				}
				else taskSol.setPoints(BigDecimal.ZERO);
			}
			solutionTest.addSolutionTask(taskSol);
		}
		if (taskSolution instanceof TaskProgrammingSolution) {
			TaskProgrammingSolution taskSol = (TaskProgrammingSolution) taskSolution;
			TaskProgramming taskProgramming = (TaskProgramming) taskSol.getTask();
			SolutionConfig solutionConfig = new SolutionConfig();
			JSONObject jsonObject;
			String userDirectory = solutionTest.getTest().getName() + '_' + solutionTest.getAttempt() + '_' + solutionTest.getUser().getId() + '_' + UUID.randomUUID().toString().substring(0, 4) + '/';
			userDirectory = userDirectory.replaceAll(" ", "");
			Set<TaskProgrammingDetail> taskProgrammingDetails = taskProgramming.getProgrammingDetailSet();
			for (TaskProgrammingDetail taskProgrammingDetail : taskProgrammingDetails) {
				log.info(taskProgrammingDetail.getLanguage() + " " + taskSol.getLanguage() + " " + (taskProgrammingDetail.getLanguage().toString().equals(taskSol.getLanguage())));
				if ((taskProgrammingDetail.getLanguage().toString().equals(taskSol.getLanguage()))) {
					log.info("aaa");
					if (((TaskProgrammingSolution) taskSolution).getLanguage().equals(ProgrammingLanguages.JAVA.toString())) {
						jsonObject = solutionConfig.createJavaConfig(taskProgrammingDetail.getSolutionClassName(), taskProgrammingDetail.getTestClassName(), "restricted_list_java");
						FileUtils.writeStringToFile(new File(dir + userDirectory + taskProgrammingDetail.getSolutionClassName()), taskSol.getAnswerCode());
						FileUtils.writeStringToFile(new File(dir + userDirectory + taskProgrammingDetail.getTestClassName()), taskProgrammingDetail.getTestCode());
						FileUtils.writeStringToFile(new File(dir + userDirectory + "restricted_list_java"), taskProgrammingDetail.getRestrictedList());
						FileUtils.writeStringToFile(new File(dir + userDirectory + CONFIG), jsonObject.toJSONString());
					}
					else if ("CPP".equals(((TaskProgrammingSolution) taskSolution).getLanguage())) {
						jsonObject = solutionConfig.createCppConfig(taskProgrammingDetail.getSolutionClassName(), taskProgrammingDetail.getTestClassName(), "restricted_list_cpp", "-w");
						FileUtils.writeStringToFile(new File(dir + userDirectory + taskProgrammingDetail.getSolutionClassName()), taskSol.getAnswerCode());
						FileUtils.writeStringToFile(new File(dir + userDirectory + taskProgrammingDetail.getTestClassName()), taskProgrammingDetail.getTestCode());
						FileUtils.writeStringToFile(new File(dir + userDirectory + "restricted_list_cpp"), taskProgrammingDetail.getRestrictedList());
						FileUtils.writeStringToFile(new File(dir + userDirectory + CONFIG), jsonObject.toJSONString());
					}
					else if (((TaskProgrammingSolution) taskSolution).getLanguage().equals(ProgrammingLanguages.PYTHON3.toString())) {
						jsonObject = solutionConfig.createPythonConfig(taskProgrammingDetail.getSolutionClassName(), taskProgrammingDetail.getTestClassName(), "restricted_list_python");
						FileUtils.writeStringToFile(new File(dir + userDirectory + taskProgrammingDetail.getSolutionClassName()), taskSol.getAnswerCode());
						FileUtils.writeStringToFile(new File(dir + userDirectory + taskProgrammingDetail.getTestClassName()), taskProgrammingDetail.getTestCode());
						FileUtils.writeStringToFile(new File(dir + userDirectory + "restricted_list_python"), taskProgrammingDetail.getRestrictedList());
						FileUtils.writeStringToFile(new File(dir + userDirectory + CONFIG), jsonObject.toJSONString());
					}
				}
			}
			executeCommand("ruby " + dir + "skrypt.rb \"" + dir + "\" \"" + userDirectory + "\"");

			JSONParser parser = new JSONParser();
			Object result = parser.parse(new FileReader(resultDir + userDirectory + OUTPUT));
			jsonObject = (JSONObject) result;
			if (jsonObject.get("time") != null) {
				BigDecimal all = BigDecimal.valueOf((Long) jsonObject.get("all"));
				BigDecimal passed = BigDecimal.valueOf((Long) jsonObject.get("passed"));
				BigDecimal resultTest = (passed.divide(all, MathContext.DECIMAL128).setScale(4, RoundingMode.HALF_UP)); //TODO dodac czas rozwiazania do statystyk
				BigDecimal points = resultTest.multiply(taskSol.getTask().getMaxPoints(), MathContext.DECIMAL128).setScale(4, RoundingMode.HALF_UP);
				taskSol.setPoints(points);
				if (jsonObject.get("output") != null) {
					String output = (String) jsonObject.get("output");
					taskSol.setFailedUnitTest(output);
				}
				solutionTest.setPoints(solutionTest.getPoints().add(points));
			}
			else {
				CompilationError compilationError = new CompilationError();

				for (CompilationErrorTypes type : CompilationErrorTypes.values()) {
					if (jsonObject.get(type.getValue()) != null) {
						compilationError.setType(type);
						compilationError.setError(jsonObject.get(type.getValue()).toString());
					}
				}
				taskSol.setCompilationError(compilationError);
				taskSol.setPoints(BigDecimal.ZERO);
			}
			FileUtils.deleteDirectory(new File(resultDir + userDirectory));
			FileUtils.deleteDirectory(new File(dir + userDirectory));
			solutionTest.addSolutionTask(taskSol);
		}
		if (taskSolution instanceof TaskSqlSolution) {
			TaskSqlSolution taskSqlSolution = (TaskSqlSolution) taskSolution;
			TaskSql taskSql = (TaskSql) taskSqlSolution.getTask();
			SolutionConfig solutionConfig = new SolutionConfig();
			JSONObject jsonObject;
			JSONObject source = new JSONObject();
			source.put("task0", taskSqlSolution.getSqlAnswer());
			JSONObject tests = new JSONObject();
			JSONArray array = new JSONArray();
			array.add("type0");
			array.add(taskSql.getSqlAnswer());
			tests.put("task0", array);
			String userDirectory = solutionTest.getTest().getName() + '_' + solutionTest.getAttempt() + '_' + solutionTest.getUser().getId() + '_' + UUID.randomUUID().toString().substring(0, 4) + '/';
			userDirectory = userDirectory.replaceAll(" ", "");
			jsonObject = solutionConfig.createSqlConfig("sources.json", "preparations.txt", "tests.json", "restricted_list_sql");
			FileUtils.writeStringToFile(new File(dir + userDirectory + "tests.json"), tests.toJSONString());
			FileUtils.writeStringToFile(new File(dir + userDirectory + "sources.json"), source.toJSONString());
			FileUtils.writeStringToFile(new File(dir + userDirectory + "preparations.txt"), taskSql.getPreparations());
			FileUtils.writeStringToFile(new File(dir + userDirectory + "restricted_list_sql"), "drop");
			FileUtils.writeStringToFile(new File(dir + userDirectory + CONFIG), jsonObject.toJSONString());
			executeCommand("ruby " + dir + "skrypt.rb \"" + dir + "\" \"" + userDirectory + "\"");

			JSONParser parser = new JSONParser();
			Object result = parser.parse(new FileReader(resultDir + userDirectory + OUTPUT));
			jsonObject = (JSONObject) result;
			if (jsonObject.get("time") != null) {
				BigDecimal all = BigDecimal.valueOf((Long) jsonObject.get("all"));
				BigDecimal passed = BigDecimal.valueOf((Long) jsonObject.get("passed"));
				BigDecimal resultTest = (passed.divide(all, MathContext.DECIMAL128).setScale(4, RoundingMode.HALF_UP));
				BigDecimal points = resultTest.multiply(taskSqlSolution.getTask().getMaxPoints(), MathContext.DECIMAL128).setScale(4, RoundingMode.HALF_UP);
				taskSqlSolution.setPoints(points);
				solutionTest.setPoints(solutionTest.getPoints().add(points));
			}
			else {
				CompilationError compilationError = new CompilationError();

				for (CompilationErrorTypes type : CompilationErrorTypes.values()) {
					if (jsonObject.get(type.getValue()) != null) {
						compilationError.setType(type);
						compilationError.setError(jsonObject.get(type.getValue()).toString());
					}
				}
				taskSqlSolution.setCompilationError(compilationError);
				taskSqlSolution.setPoints(BigDecimal.ZERO);
			}
			FileUtils.deleteDirectory(new File(resultDir + userDirectory));
			FileUtils.deleteDirectory(new File(dir + userDirectory));
			solutionTest.addSolutionTask(taskSqlSolution);
		}

	}

	public SolutionTest create(SolutionTest solutionTest, SolutionTestForm solutionTestForm) {
		try {
			List<SolutionTaskForm> solutionTaskForms = solutionTestForm.getTasks();
			solutionTest.setPoints(BigDecimal.ZERO);
			this.taskNo = 0;
			for (SolutionTaskForm solutionTaskForm : solutionTaskForms)
				if (solutionTaskForm.getTaskType() == SolutionTaskForm.CLOSEDTASK) {
					TaskClosedSolution taskClosedSolution = new TaskClosedSolution(solutionTaskForm.getTask());
					TreeMap<String, Boolean> answers = new TreeMap<>();
					answers.putAll(solutionTaskForm.getAnswers());
					taskClosedSolution.setAnswers(answers);
					addTaskSolutionToTest(solutionTest, taskClosedSolution);
				}
				else if (solutionTaskForm.getTaskType() == SolutionTaskForm.OPENTASK) {
					TaskOpenSolution taskOpenSolution = new TaskOpenSolution(solutionTaskForm.getTask());
					taskOpenSolution.setAnswer(solutionTaskForm.getAnswer());
					addTaskSolutionToTest(solutionTest, taskOpenSolution);
				}
				else if (solutionTaskForm.getTaskType() == SolutionTaskForm.PROGRAMMINGTASK) {
					log.info(solutionTaskForm.toString());
					TaskProgrammingSolution taskProgrammingSolution = new TaskProgrammingSolution(solutionTaskForm.getTask());
					taskProgrammingSolution.setAnswerCode(solutionTaskForm.getAnswerCode());
					taskProgrammingSolution.setLanguage(solutionTaskForm.getLanguage());
					addTaskSolutionToTest(solutionTest, taskProgrammingSolution);
				}
				else if (solutionTaskForm.getTaskType() == SolutionTaskForm.SQLTASK) {
					TaskSqlSolution taskSqlSolution = new TaskSqlSolution(solutionTaskForm.getTask());
					taskSqlSolution.setSqlAnswer(solutionTaskForm.getAnswerCode());
					addTaskSolutionToTest(solutionTest, taskSqlSolution);
				}
			return solutionTest;
		}
		catch (ParseException | IOException | InterruptedException e) {
			throw new SolutionTestException(e.getMessage());
		}
	}

	public Collection<SolutionTest> getSolutionTestsByUser(User user) {
		return solutionTestRepository.findSolutionTestsByUserAndSolutionStatus(user, SolutionStatus.FINISHED);
	}

	public Collection<SolutionTest> getSolutionTestsByTest(Test test) {
		return solutionTestRepository.findSolutionTestsByTestAndSolutionStatusOrderByPointsDesc(test, SolutionStatus.FINISHED);
	}

	public Optional<SolutionTest> findSolutionTestByTestAndUserAndSolutionStatus(Test test, User user, SolutionStatus solutionStatus) {
		return solutionTestRepository.findSolutionTestByTestAndUserAndSolutionStatus(test, user, solutionStatus);
	}

	public SolutionTestForm createFormWithExistingSolution(SolutionTest solutionTest) {
		this.taskNo = 0;
		SolutionTestForm solutionTestForm = new SolutionTestForm();

		solutionTestForm.setName(solutionTest.getTest().getName());
		solutionTestForm.setSolutionId(solutionTest.getId());
		List<SolutionTaskForm> solutionTaskFormList = solutionTest.getSolutionTasks().stream().map(SolutionTaskForm::new).collect(Collectors.toList());
		solutionTestForm.setTasks(solutionTaskFormList);
		this.taskNo = 0;
		return solutionTestForm;
	}

	public String executeCommand(String command) throws IOException, InterruptedException {
		log.info(command);
		StringBuilder output = new StringBuilder();
		Process p;
		p = Runtime.getRuntime().exec(command);
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		String line;
		while ((line = reader.readLine()) != null) {
			output.append(line).append('\n');
		}
		return output.toString();

	}
}
