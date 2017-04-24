package com.zespolowka.entity.solutionTest;

import com.zespolowka.entity.createTest.Test;
import com.zespolowka.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SolutionTest {
    private static final Logger logger = LoggerFactory.getLogger(SolutionTest.class);
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(targetEntity = Test.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "idTest")
    private Test test;
    private Integer attempt;
    private LocalDateTime beginSolution;
    private LocalDateTime endSolution;
    private Float points;
    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private SolutionStatus solutionStatus;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TaskSolution> solutionTasks;

    public SolutionTest() {
        this.solutionTasks = new ArrayList<>();
        this.solutionStatus = SolutionStatus.OPEN;
        this.points = 0.0f;
    }

    public SolutionTest(Test test, User user) {
        this.solutionTasks = new ArrayList<>();
        this.solutionStatus = SolutionStatus.OPEN;
        this.test = test;
        this.points = 0.0f;
        this.user = user;
    }

    public Long secondsToEnd() {
        Timestamp timestamp = Timestamp.valueOf(beginSolution.plusMinutes(test.getTimePerAttempt()));
        Timestamp timestamp1 = Timestamp.valueOf(LocalDateTime.now());
        Long value = timestamp.getTime() - timestamp1.getTime();
        return value / 1000L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }

    public LocalDateTime getBeginSolution() {
        return beginSolution;
    }

    public void setBeginSolution(LocalDateTime beginSolution) {
        this.beginSolution = beginSolution;
    }

    public LocalDateTime getEndSolution() {
        return endSolution;
    }

    public void setEndSolution(LocalDateTime endSolution) {
        this.endSolution = endSolution;
    }

    public Float getPoints() {
        return points;
    }

    public void setPoints(Float points) {
        this.points = points;
    }

    public List<TaskSolution> getSolutionTasks() {
        return solutionTasks;
    }

    public void setSolutionTasks(List<TaskSolution> solutionTasks) {
        this.solutionTasks = solutionTasks;
    }

    public SolutionStatus getSolutionStatus() {
        return solutionStatus;
    }

    public void setSolutionStatus(SolutionStatus solutionStatus) {
        this.solutionStatus = solutionStatus;
    }

}
