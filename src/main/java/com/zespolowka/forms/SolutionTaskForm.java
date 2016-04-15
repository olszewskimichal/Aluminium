package com.zespolowka.forms;


import com.zespolowka.entity.createTest.Task;
import com.zespolowka.entity.createTest.TaskClosed;
import com.zespolowka.entity.createTest.TaskProgramming;
import com.zespolowka.entity.createTest.TaskProgrammingDetail;

import java.util.*;

public class SolutionTaskForm {
    public static final int CLOSEDTASK = 0;
    public static final int OPENTASK = 1;
    public static final int PROGRAMMINGTASK = 2;
    private Task task;
    private Map<String, Boolean> answers = new LinkedHashMap<>();
    private String answer = "";
    private String answerCode = "";
    private Set<String> languages = new TreeSet<>();
    private String language = new String();
    private int taskType;

    public SolutionTaskForm() {

    }

    public SolutionTaskForm(int taskType) {
        this.taskType = taskType;
    }

    public SolutionTaskForm(Task task, int taskType) {
        this.task = task;
        this.taskType = taskType;
        if (taskType == CLOSEDTASK) {
            TaskClosed taskClosed = (TaskClosed) task;
            List<Map.Entry<String, Boolean>> list = new ArrayList<>(taskClosed.getAnswers().entrySet());
            Collections.shuffle(list);
            for (Map.Entry<String, Boolean> entry : list) {
                answers.put(entry.getKey(), false);
            }
        }
        if (taskType == PROGRAMMINGTASK) {
            TaskProgramming taskProgramming = (TaskProgramming) task;
            Set<TaskProgrammingDetail> taskProgrammingDetails = taskProgramming.getProgrammingDetailSet();
            for (TaskProgrammingDetail taskProgrammingDetail : taskProgrammingDetails) {
                languages.add(taskProgrammingDetail.getLanguage().toString());
            }
        }
    }

    public Task getTask() {
        return task;
    }

    public Map<String, Boolean> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Boolean> answers) {
        this.answers = answers;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerCode() {
        return answerCode;
    }

    public void setAnswerCode(String answerCode) {
        this.answerCode = answerCode;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "SolutionTaskForm{" +
                "task=" + task +
                ", answers=" + answers +
                ", answer='" + answer + '\'' +
                ", answerCode='" + answerCode + '\'' +
                ", languages='" + languages + '\'' +
                ", taskType=" + taskType +
                '}';
    }
}
