package com.zespolowka.service;


import com.zespolowka.entity.createTest.*;
import com.zespolowka.entity.solutionTest.*;
import com.zespolowka.entity.solutionTest.config.SolutionConfig;
import com.zespolowka.entity.user.User;
import com.zespolowka.forms.NewMessageForm;
import com.zespolowka.forms.SolutionTaskForm;
import com.zespolowka.forms.SolutionTestForm;
import com.zespolowka.repository.SolutionTestRepository;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolutionTestService {
    private static final Logger logger = LoggerFactory.getLogger(SolutionTestService.class);
    private static final String OUTPUT = "output.json";
    private static final String CONFIG = "config.json";

    private final SolutionTestRepository solutionTestRepository;
    private final HttpSession httpSession;
    private final NotificationService notificationService;
    private final Environment environment;
    private final UserService UserService;

    private int taskNo = 0;

    private String dir = "/var/www/Aluminium/Team_Programming_Rewritten/";

    private String resultDir = "/tmp/";

    @Autowired
    public SolutionTestService(SolutionTestRepository solutionTestRepository, HttpSession httpSession, NotificationService notificationService, Environment environment, UserService UserService) {
        this.solutionTestRepository = solutionTestRepository;
        this.httpSession = httpSession;
        this.notificationService = notificationService;
        this.environment = environment;
        this.UserService = UserService;
    }

    public Integer countSolutionTestsByTestAndSolutionStatus(Test test, SolutionStatus solutionStatus) {
        return solutionTestRepository.countSolutionTestsByTestAndSolutionStatus(test, solutionStatus);
    }

    public List<SolutionTest> getSolutionsWithTheBestResult(User user) {
        return solutionTestRepository.getSolutionsWithTheBestResult(user, SolutionStatus.FINISHED);
    }

    public Integer countSolutionTestsByUserAndTest(User user, Test test) {
        return solutionTestRepository.countSolutionTestsByUserAndTestAndSolutionStatus(user, test,
                SolutionStatus.FINISHED);
    }

    public SolutionTest getSolutionTestById(long id) {
        return solutionTestRepository.findSolutionTestById(id);
    }


    public SolutionTest create(SolutionTest solutionTest, SolutionStatus solutionStatus) {
        try {
            taskNo = 0;
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m:s");
            LocalDateTime dateTime = LocalDateTime.now();
            solutionTest.setEndSolution(LocalDateTime.parse(
                    dateTime.getYear() + "/" + dateTime.getMonthValue() + '/' + dateTime.getDayOfMonth() + ' ' + dateTime
                            .getHour() + ':' + dateTime.getMinute() + ':' + dateTime.getSecond(), dateTimeFormatter));
            solutionTest.setSolutionStatus(solutionStatus);
            if (solutionTest.getSolutionStatus() == SolutionStatus.FINISHED) {
                ResourceBundle messages = ResourceBundle.getBundle("messages", LocaleContextHolder.getLocale());
                NewMessageForm newMessageForm = new NewMessageForm();
                newMessageForm.setReceivers(solutionTest.getUser().getEmail());
                newMessageForm.setTopic(messages.getString("results.topic") + " " + solutionTest.getTest().getName());
                newMessageForm.setMessage(messages.getString(
                        "results.message") + " " + solutionTest.getPoints() + " / " + solutionTest.getTest()
                        .getMaxPoints());
                User system = UserService.getUserById(1L)
                        .orElseThrow(
                                () -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", 1)));
                newMessageForm.setSender(system);
                notificationService.sendMessage(newMessageForm);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            logger.info(solutionTest.toString());
            logger.info(solutionStatus.toString());
        }

        return solutionTestRepository.saveAndFlush(solutionTest);
    }

    public SolutionTestForm createForm(Test test, User user) {
        SolutionTestForm solutionTestForm = new SolutionTestForm();
        try {
            SolutionTest solutionTest;
            Optional<SolutionTest> solutionTest2 = findSolutionTestByTestAndUserAndSolutionStatus(test, user,
                    SolutionStatus.OPEN);
            if (!solutionTest2.isPresent()) {
                solutionTest = new SolutionTest(test, user);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m:s");
                LocalDateTime dateTime = LocalDateTime.now();
                solutionTest.setBeginSolution(LocalDateTime.parse(
                        dateTime.getYear() + "/" + dateTime.getMonthValue() + '/' + dateTime.getDayOfMonth() + ' ' + dateTime
                                .getHour() + ':' + dateTime.getMinute() + ':' + dateTime.getSecond(),
                        dateTimeFormatter));
                solutionTest.setAttempt(countSolutionTestsByUserAndTest(user, test) + 1);
                solutionTest.setSolutionStatus(SolutionStatus.OPEN);
                solutionTestRepository.saveAndFlush(solutionTest);
                solutionTestRepository.flush();
            } else solutionTest = solutionTest2.get();
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
                } else if (task instanceof TaskOpen) {
                    solutionTaskFormList.add(new SolutionTaskForm(task, SolutionTaskForm.OPENTASK));
                } else if (task instanceof TaskProgramming) {
                    solutionTaskFormList.add(new SolutionTaskForm(task, SolutionTaskForm.PROGRAMMINGTASK));
                } else if (task instanceof TaskSql) {
                    solutionTaskFormList.add(new SolutionTaskForm(task, SolutionTaskForm.SQLTASK));
                }
            }
            httpSession.setAttribute("integerList", integerList);
            solutionTestRepository.saveAndFlush(solutionTest);
            solutionTestForm.setTasks(solutionTaskFormList);
            this.taskNo = 0;
        } catch (Exception e) {
            logger.info(test.toString());
            logger.info(user.toString());
            logger.info(solutionTestForm.toString());
        }
        return solutionTestForm;
    }

    public void addTaskSolutionToTest(SolutionTest solutionTest, TaskSolution taskSolution) throws IOException, ParseException {
        try {
            List<Long> integerList = (List<Long>) httpSession.getAttribute("integerList");
            if (environment.getActiveProfiles().length > 0 && environment.getActiveProfiles()[0].equals("prod")) {
                Long minimumId = Collections.min(integerList);
                taskSolution.setTask(solutionTest.getTest()
                        .getTasks()
                        .get(integerList.get(taskNo++).intValue() - minimumId.intValue()));
            } else taskSolution.setTask(solutionTest.getTest().getTasks().get(taskNo++));
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
                        if ((stringBooleanEntry.getValue() != null && stringBooleanEntry.getValue()) && (!correctAnswers
                                .get(stringBooleanEntry.getKey()))) {
                            theSame = false;
                        } else if ((stringBooleanEntry.getValue() == null || !stringBooleanEntry.getValue()) && (correctAnswers
                                .get(stringBooleanEntry.getKey()))) {
                            theSame = false;
                        }
                    }
                    if (theSame) {
                        taskSol.setPoints(taskClo.getMax_points());
                    } else taskSol.setPoints(0.0f);
                } else {
                    Float pointsDivide = 0.0f;
                    Float noCorrectAnswers = 0.0f;
                    Boolean chooseIncorect = false;
                    for (Map.Entry<String, Boolean> stringBooleanEntry : userAnswers.entrySet()) {
                        if (stringBooleanEntry.getValue() == null) userAnswers.put(stringBooleanEntry.getKey(), false);
                        if (correctAnswers.get(stringBooleanEntry.getKey())) pointsDivide++;
                        if (stringBooleanEntry.getValue() && !correctAnswers.get(stringBooleanEntry.getKey())) {
                            chooseIncorect = true;
                            break;
                        } else if (stringBooleanEntry.getValue()
                                .equals(correctAnswers.get(stringBooleanEntry.getKey())) && correctAnswers.get(
                                stringBooleanEntry.getKey()))
                            noCorrectAnswers++;
                    }
                    if (chooseIncorect || noCorrectAnswers < 1.0F) {
                        taskSol.setPoints(0.0f);
                    } else {
                        taskSol.setPoints(taskClo.getMax_points() / (pointsDivide / noCorrectAnswers));
                    }
                }
                solutionTest.setPoints(solutionTest.getPoints() + taskSol.getPoints());
                solutionTest.getSolutionTasks().add(taskSol);
            }
            if (taskSolution instanceof TaskOpenSolution) {
                TaskOpenSolution taskSol = (TaskOpenSolution) taskSolution;
                TaskOpen taskOp = (TaskOpen) taskSol.getTask();
                if (!taskOp.getCaseSens()) {
                    if (taskSol.getAnswer().toUpperCase().equals(taskOp.getAnswer().toUpperCase())) {
                        solutionTest.setPoints(solutionTest.getPoints() + taskOp.getMax_points());
                        taskSol.setPoints(taskOp.getMax_points());
                    } else taskSol.setPoints(0.0f);
                } else {
                    if (taskSol.getAnswer().equals(taskOp.getAnswer())) {
                        solutionTest.setPoints(solutionTest.getPoints() + taskOp.getMax_points());
                        taskSol.setPoints(taskOp.getMax_points());
                    } else taskSol.setPoints(0.0f);
                }
                solutionTest.getSolutionTasks().add(taskSol);
            }
            if (taskSolution instanceof TaskProgrammingSolution) {
                TaskProgrammingSolution taskSol = (TaskProgrammingSolution) taskSolution;
                TaskProgramming taskProgramming = (TaskProgramming) taskSol.getTask();
                SolutionConfig solutionConfig = new SolutionConfig();
                JSONObject jsonObject;
                String userDirectory = solutionTest.getTest()
                        .getName() + '_' + solutionTest.getAttempt() + '_' + solutionTest.getUser().getId() + '_' + UUID
                        .randomUUID()
                        .toString()
                        .substring(0, 4) + '/';
                userDirectory = userDirectory.replaceAll(" ", "");
                Set<TaskProgrammingDetail> taskProgrammingDetails = taskProgramming.getProgrammingDetailSet();
                for (TaskProgrammingDetail taskProgrammingDetail : taskProgrammingDetails) {
                    logger.info(
                            taskProgrammingDetail.getLanguage() + " " + taskSol.getLanguage() + " " + (taskProgrammingDetail
                                    .getLanguage()
                                    .toString()
                                    .equals(taskSol.getLanguage().toString())));
                    if ((taskProgrammingDetail.getLanguage().toString().equals(taskSol.getLanguage().toString()))) {
                        logger.info("aaa");
                        if (((TaskProgrammingSolution) taskSolution).getLanguage()
                                .equals(ProgrammingLanguages.JAVA.toString())) {
                            jsonObject = solutionConfig.createJavaConfig(taskProgrammingDetail.getSolutionClassName(),
                                    taskProgrammingDetail.getTestClassName(), "restricted_list_java");
                            FileUtils.writeStringToFile(
                                    new File(dir + userDirectory + taskProgrammingDetail.getSolutionClassName()),
                                    taskSol.getAnswerCode());
                            FileUtils.writeStringToFile(
                                    new File(dir + userDirectory + taskProgrammingDetail.getTestClassName()),
                                    taskProgrammingDetail.getTestCode());
                            FileUtils.writeStringToFile(new File(dir + userDirectory + "restricted_list_java"),
                                    taskProgrammingDetail.getRestrictedList());
                            FileUtils.writeStringToFile(new File(dir + userDirectory + CONFIG),
                                    jsonObject.toJSONString());
                        } else if (((TaskProgrammingSolution) taskSolution).getLanguage().equals("CPP")) {
                            jsonObject = solutionConfig.createCppConfig(taskProgrammingDetail.getSolutionClassName(),
                                    taskProgrammingDetail.getTestClassName(), "restricted_list_cpp", "-w");
                            FileUtils.writeStringToFile(
                                    new File(dir + userDirectory + taskProgrammingDetail.getSolutionClassName()),
                                    taskSol.getAnswerCode());
                            FileUtils.writeStringToFile(
                                    new File(dir + userDirectory + taskProgrammingDetail.getTestClassName()),
                                    taskProgrammingDetail.getTestCode());
                            FileUtils.writeStringToFile(new File(dir + userDirectory + "restricted_list_cpp"),
                                    taskProgrammingDetail.getRestrictedList());
                            FileUtils.writeStringToFile(new File(dir + userDirectory + CONFIG),
                                    jsonObject.toJSONString());
                        } else if (((TaskProgrammingSolution) taskSolution).getLanguage()
                                .equals(ProgrammingLanguages.PYTHON3.toString())) {
                            jsonObject = solutionConfig.createPythonConfig(taskProgrammingDetail.getSolutionClassName(),
                                    taskProgrammingDetail.getTestClassName(), "restricted_list_python");
                            FileUtils.writeStringToFile(
                                    new File(dir + userDirectory + taskProgrammingDetail.getSolutionClassName()),
                                    taskSol.getAnswerCode());
                            FileUtils.writeStringToFile(
                                    new File(dir + userDirectory + taskProgrammingDetail.getTestClassName()),
                                    taskProgrammingDetail.getTestCode());
                            FileUtils.writeStringToFile(new File(dir + userDirectory + "restricted_list_python"),
                                    taskProgrammingDetail.getRestrictedList());
                            FileUtils.writeStringToFile(new File(dir + userDirectory + CONFIG),
                                    jsonObject.toJSONString());
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
                    BigDecimal time = BigDecimal.valueOf((Double) jsonObject.get("time"));
                    BigDecimal resultTest = (passed.divide(all, MathContext.DECIMAL128)
                            .setScale(4, RoundingMode.HALF_UP)); //TODO dodac czas rozwiazania do statystyk
                    BigDecimal points = resultTest.multiply(BigDecimal.valueOf(taskSol.getTask().getMax_points()),
                            MathContext.DECIMAL128).setScale(4, RoundingMode.HALF_UP);
                    taskSol.setPoints(points.floatValue());
                    if (jsonObject.get("output") != null) {
                        String output = (String) jsonObject.get("output");
                        taskSol.setFailedUnitTest(output);
                    }
                    solutionTest.setPoints(solutionTest.getPoints() + points.floatValue());
                } else {
                    CompilationError compilationError = new CompilationError();

                    for (CompilationErrorTypes type : CompilationErrorTypes.values()) {
                        if (jsonObject.get(type.getValue()) != null) {
                            compilationError.setType(type);
                            compilationError.setError(jsonObject.get(type.getValue()).toString());
                        }
                    }
                    taskSol.setCompilationError(compilationError);
                    taskSol.setPoints(0.0f);
                }
                FileUtils.deleteDirectory(new File(resultDir + userDirectory));
                FileUtils.deleteDirectory(new File(dir + userDirectory));
                solutionTest.getSolutionTasks().add(taskSol);
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
                String userDirectory = solutionTest.getTest()
                        .getName() + '_' + solutionTest.getAttempt() + '_' + solutionTest.getUser().getId() + '_' + UUID
                        .randomUUID()
                        .toString()
                        .substring(0, 4) + '/';
                userDirectory = userDirectory.replaceAll(" ", "");
                jsonObject = solutionConfig.createSqlConfig("sources.json", "preparations.txt", "tests.json",
                        "restricted_list_sql");
                FileUtils.writeStringToFile(new File(dir + userDirectory + "tests.json"), tests.toJSONString());
                FileUtils.writeStringToFile(new File(dir + userDirectory + "sources.json"), source.toJSONString());
                FileUtils.writeStringToFile(new File(dir + userDirectory + "preparations.txt"),
                        taskSql.getPreparations());
                FileUtils.writeStringToFile(new File(dir + userDirectory + "restricted_list_sql"), "drop");
                FileUtils.writeStringToFile(new File(dir + userDirectory + CONFIG), jsonObject.toJSONString());
                executeCommand("ruby " + dir + "skrypt.rb \"" + dir + "\" \"" + userDirectory + "\"");

                JSONParser parser = new JSONParser();
                Object result = parser.parse(new FileReader(resultDir + userDirectory + OUTPUT));
                jsonObject = (JSONObject) result;
                if (jsonObject.get("time") != null) {
                    BigDecimal all = BigDecimal.valueOf((Long) jsonObject.get("all"));
                    BigDecimal passed = BigDecimal.valueOf((Long) jsonObject.get("passed"));
                    BigDecimal resultTest = (passed.divide(all, MathContext.DECIMAL128)
                            .setScale(4, RoundingMode.HALF_UP));
                    BigDecimal points = resultTest.multiply(
                            BigDecimal.valueOf(taskSqlSolution.getTask().getMax_points()), MathContext.DECIMAL128)
                            .setScale(4, RoundingMode.HALF_UP);
                    taskSqlSolution.setPoints(points.floatValue());
                    solutionTest.setPoints(solutionTest.getPoints() + points.floatValue());
                } else {
                    CompilationError compilationError = new CompilationError();

                    for (CompilationErrorTypes type : CompilationErrorTypes.values()) {
                        if (jsonObject.get(type.getValue()) != null) {
                            compilationError.setType(type);
                            compilationError.setError(jsonObject.get(type.getValue()).toString());
                        }
                    }
                    taskSqlSolution.setCompilationError(compilationError);
                    taskSqlSolution.setPoints(0.0f);
                }
                FileUtils.deleteDirectory(new File(resultDir + userDirectory));
                FileUtils.deleteDirectory(new File(dir + userDirectory));
                solutionTest.getSolutionTasks().add(taskSqlSolution);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            logger.info(taskSolution.toString());
            logger.info(solutionTest.toString());
            logger.info(httpSession.getAttribute("integerList").toString());
        }
    }

    public SolutionTest create(SolutionTest solutionTest, SolutionTestForm solutionTestForm) throws IOException, ParseException {
        try {
            List<SolutionTaskForm> solutionTaskForms = solutionTestForm.getTasks();
            solutionTest.setSolutionTasks(new ArrayList<>());
            solutionTest.setPoints(0.0f);
            this.taskNo = 0;
            for (SolutionTaskForm solutionTaskForm : solutionTaskForms)
                if (solutionTaskForm.getTaskType() == SolutionTaskForm.CLOSEDTASK) {
                    TaskClosedSolution taskClosedSolution = new TaskClosedSolution(solutionTaskForm.getTask());
                    TreeMap<String, Boolean> answers = new TreeMap<>();
                    answers.putAll(solutionTaskForm.getAnswers());
                    taskClosedSolution.setAnswers(answers);
                    addTaskSolutionToTest(solutionTest, taskClosedSolution);
                } else if (solutionTaskForm.getTaskType() == SolutionTaskForm.OPENTASK) {
                    TaskOpenSolution taskOpenSolution = new TaskOpenSolution(solutionTaskForm.getTask());
                    taskOpenSolution.setAnswer(solutionTaskForm.getAnswer());
                    addTaskSolutionToTest(solutionTest, taskOpenSolution);
                } else if (solutionTaskForm.getTaskType() == SolutionTaskForm.PROGRAMMINGTASK) {
                    logger.info(solutionTaskForm.toString());
                    TaskProgrammingSolution taskProgrammingSolution = new TaskProgrammingSolution(
                            solutionTaskForm.getTask());
                    taskProgrammingSolution.setAnswerCode(solutionTaskForm.getAnswerCode());
                    taskProgrammingSolution.setLanguage(solutionTaskForm.getLanguage());
                    addTaskSolutionToTest(solutionTest, taskProgrammingSolution);
                } else if (solutionTaskForm.getTaskType() == SolutionTaskForm.SQLTASK) {
                    TaskSqlSolution taskSqlSolution = new TaskSqlSolution(solutionTaskForm.getTask());
                    taskSqlSolution.setSqlAnswer(solutionTaskForm.getAnswerCode());
                    addTaskSolutionToTest(solutionTest, taskSqlSolution);
                }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            logger.info(solutionTest.toString());
            logger.info(solutionTestForm.toString());
        }
        return solutionTest;
    }

    public Collection<SolutionTest> getSolutionTestsByUser(User user) {
        return solutionTestRepository.findSolutionTestsByUserAndSolutionStatus(user, SolutionStatus.FINISHED);
    }

    public Collection<SolutionTest> getSolutionTestsByTest(Test test) {
        return solutionTestRepository.findSolutionTestsByTestAndSolutionStatusOrderByPointsDesc(test,
                SolutionStatus.FINISHED);
    }

    public Optional<SolutionTest> findSolutionTestByTestAndUserAndSolutionStatus(Test test, User user, SolutionStatus solutionStatus) {
        return solutionTestRepository.findSolutionTestByTestAndUserAndSolutionStatus(test, user, solutionStatus);
    }

    public SolutionTestForm createFormWithExistingSolution(SolutionTest solutionTest) {
        this.taskNo = 0;
        SolutionTestForm solutionTestForm = new SolutionTestForm();
        try {
            solutionTestForm.setName(solutionTest.getTest().getName());
            solutionTestForm.setSolutionId(solutionTest.getId());
            List<SolutionTaskForm> solutionTaskFormList = solutionTest.getSolutionTasks()
                    .stream()
                    .map(SolutionTaskForm::new)
                    .collect(Collectors.toList());
            solutionTestForm.setTasks(solutionTaskFormList);
            this.taskNo = 0;
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            logger.info(solutionTest.toString());
            logger.info(solutionTestForm.toString());
        }
        return solutionTestForm;
    }

    public String executeCommand(String command) {
        logger.info(command);
        StringBuilder output = new StringBuilder();
        try {
            Process p;
            try {
                p = Runtime.getRuntime().exec(command);
                p.waitFor();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append('\n');
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            logger.info(command);
            logger.info(output.toString());
        }
        return output.toString();

    }

    @Override
    public String toString() {
        return "SolutionTestServiceImpl{" +
                "solutionTestRepository=" + solutionTestRepository +
                ", taskNo=" + taskNo +
                ", dir='" + dir + '\'' +
                ", resultDir='" + resultDir + '\'' +
                '}';
    }
}
