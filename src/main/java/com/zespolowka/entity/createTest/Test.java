package com.zespolowka.entity.createTest;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@javax.persistence.Table(name = "TestTable")
public class Test {
    @GeneratedValue
    @Id
    @Column(name = "idTest")
    private Long id;
    private String name;
    @Lob
    @Column(length = 1000)
    private String messageFAQ = "";
    private Long attempts;
    private LocalDate beginDate;
    private LocalDate endDate;
    private Float maxPoints;
    private String password = "";
    private Integer timePerAttempt;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Task> tasks;


    public Test() {
        this.tasks = new ArrayList<>();
        this.maxPoints = 0.0f;
        this.password = "";
    }

    public Test(final String name, final Long attempts, final LocalDate beginDate, final LocalDate endDate, final List<Task> tasks, final String messageFAQ) {
        this.name = name;
        this.attempts = attempts;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.tasks = tasks;
        this.maxPoints = 0.0f;
        this.messageFAQ = messageFAQ;
    }

    public void addTaskToTest(final Task task) {
        tasks.add(task);
        updateMaxPoints(task.getMax_points());
    }

    public Boolean isOpenTest() {
        return password.length() <= 0;
    }

    public void updateMaxPoints(Float points) {
        this.maxPoints += points;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMessageFAQ() {
        return messageFAQ;
    }

    public void setMessageFAQ(final String messageFAQ) {
        this.messageFAQ = messageFAQ;
    }

    public Long getAttempts() {
        return attempts;
    }

    public void setAttempts(final Long attempts) {
        this.attempts = attempts;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(final LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Float getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Float maxPoints) {
        this.maxPoints = maxPoints;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTimePerAttempt() {
        return timePerAttempt;
    }

    public void setTimePerAttempt(Integer timePerAttempt) {
        this.timePerAttempt = timePerAttempt;
    }

    @Override
    public String toString() {
        return "Test{" +
                "attempts=" + attempts +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", messageFAQ='" + messageFAQ + '\'' +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", maxPoints=" + maxPoints +
                ", password='" + password + '\'' +
                ", timePerAttempt=" + timePerAttempt +
                ", tasks=" + tasks +
                '}';
    }
}
