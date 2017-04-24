package com.zespolowka.entity.solutionTest;

import com.zespolowka.entity.createTest.Task;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Entity
public class TaskClosedSolution extends TaskSolution {
    @ElementCollection
    private Map<String, Boolean> answers = new LinkedHashMap<>();

    public TaskClosedSolution(Task task) {
        super(task);
    }

    public Map<String, Boolean> getAnswers() {
        return answers;
    }

    public void setAnswers(TreeMap<String, Boolean> answers) {
        this.answers = answers;
    }

}
