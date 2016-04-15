package com.zespolowka.entity.solutionTest;


import com.zespolowka.entity.createTest.Task;

import javax.persistence.Entity;

@Entity
public class TaskProgrammingSolution extends TaskSolution {
    private String answerCode;

    private String language;


    public TaskProgrammingSolution() {
    }

    public TaskProgrammingSolution(Task task) {
        super(task);
        this.answerCode = "";
    }

    public TaskProgrammingSolution(Task task, String answerCode) {
        super(task);
        this.answerCode = answerCode;
    }

    public String getAnswerCode() {
        return answerCode;
    }

    public void setAnswerCode(String answerCode) {
        this.answerCode = answerCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "TaskProgrammingSolution{" +
                "TaskSolution='" + super.toString() + '\'' +
                "answerCode='" + answerCode + '\'' +
                "language='" + language + '\'' +
                '}';
    }
}
