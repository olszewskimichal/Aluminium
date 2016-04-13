package com.zespolowka.entity.createTest;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Entity
public class TaskClosed extends Task {

    @ElementCollection
    private Map<String, Boolean> answers = new TreeMap<>();

    private int countingType;

    @Transient
    public final int WRONG_RESET=0;
    @Transient
    public final int COUNT_NOT_FULL=1;

    public TaskClosed() {
    }

    public TaskClosed(String question, Float max_points, TreeMap<String, Boolean> answers) {
        super(question, max_points);
        this.answers = answers;
        this.countingType=WRONG_RESET;
    }

    public TaskClosed(String question, Float max_points) {
        super(question, max_points);
    }

    public Map<String, Boolean> getAnswers() {
        return answers;
    }

    public void setAnswers(TreeMap<String, Boolean> answers) {
        this.answers = answers;
    }

    public int getCountingType() {
        return countingType;
    }

    public void setCountingType(int countingType) {
        this.countingType = countingType;
    }

    @Override
    public String toString() {
        return "TaskClosed{" +
                "Task='" + super.toString() + '\'' +
                "question='" + super.getQuestion() + '\'' +
                "countingType=" + countingType + '\'' +
                "answers=" + answers.toString() +
                '}';
    }
}
