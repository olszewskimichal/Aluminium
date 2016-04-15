package com.zespolowka.service;


import com.zespolowka.entity.createTest.*;
import com.zespolowka.entity.solutionTest.SolutionTest;
import com.zespolowka.entity.user.User;
import com.zespolowka.forms.SolutionTaskForm;
import com.zespolowka.forms.SolutionTestForm;
import com.zespolowka.repository.SolutionTestRepository;
import com.zespolowka.service.inteface.SolutionTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class SolutionTestServiceImpl implements SolutionTestService {
    private static final Logger logger = LoggerFactory.getLogger(SolutionTestService.class);
    private static final String TEST_ATTRIBUTE_NAME = "solutionTestSession";

    private final SolutionTestRepository solutionTestRepository;
    private final HttpSession httpSession;

    @Autowired
    public SolutionTestServiceImpl(SolutionTestRepository solutionTestRepository, HttpSession httpSession) {
        this.solutionTestRepository = solutionTestRepository;
        this.httpSession = httpSession;
    }

    @Override
    public Collection<SolutionTest> getSolutionTestsByUserAndTest(User user, Test test) {
        return solutionTestRepository.findSolutionTestsByUserAndTest(user, test);
    }

    @Override
    public SolutionTest getSolutionTestById(long id) {
        return solutionTestRepository.findSolutionTestById(id);
    }

    @Override
    public Collection<SolutionTest> getAllTests() {
        return solutionTestRepository.findAll();
    }

    @Override
    public SolutionTest create(SolutionTest solutionTest) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m:s");
        LocalDateTime dateTime = LocalDateTime.now();
        solutionTest.setEndSolution(LocalDateTime.parse(dateTime.getYear() + "/" + dateTime.getMonthValue() + "/" + dateTime.getDayOfMonth() + " " + dateTime.getHour() + ":" + dateTime.getMinute() + ":" + dateTime.getSecond(), dateTimeFormatter));
        return solutionTestRepository.save(solutionTest);
    }


    @Override
    public SolutionTestForm createForm(Test test, User user) {
        SolutionTest solutionTest = (SolutionTest) this.httpSession.getAttribute(TEST_ATTRIBUTE_NAME);
        logger.info(test.toString());
        if (solutionTest != null) {
        } else {
            solutionTest = new SolutionTest(test, user);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d H:m:s");
            LocalDateTime dateTime = LocalDateTime.now();
            solutionTest.setBeginSolution(LocalDateTime.parse(dateTime.getYear() + "/" + dateTime.getMonthValue() + "/" + dateTime.getDayOfMonth() + " " + dateTime.getHour() + ":" + dateTime.getMinute() + ":" + dateTime.getSecond(), dateTimeFormatter));
            solutionTest.setAttempt(getSolutionTestsByUserAndTest(user, test).size() + 1);
            this.httpSession.setAttribute(TEST_ATTRIBUTE_NAME, solutionTest);
        }
        SolutionTestForm solutionTestForm = new SolutionTestForm();
        solutionTestForm.setName(test.getName());
        solutionTestForm.setTimeToEnd(solutionTest.secondsToEnd());
        List<SolutionTaskForm> solutionTaskFormList = new ArrayList<>();
        List<Task> tasks = test.getTasks();
        Collections.shuffle(tasks);
        test.setTasks(tasks);
        for (Task task : test.getTasks()) {
            if (task instanceof TaskClosed) {
                solutionTaskFormList.add(new SolutionTaskForm(task, SolutionTaskForm.CLOSEDTASK));
            } else if (task instanceof TaskOpen) {
                solutionTaskFormList.add(new SolutionTaskForm(task, SolutionTaskForm.OPENTASK));
            } else if (task instanceof TaskProgramming) {
                solutionTaskFormList.add(new SolutionTaskForm(task, SolutionTaskForm.PROGRAMMINGTASK));
            }
        }
        solutionTestForm.setTasks(solutionTaskFormList);

        return solutionTestForm;

    }
}
