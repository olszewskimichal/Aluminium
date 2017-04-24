package com.zespolowka.forms;


import com.zespolowka.entity.createTest.Task;
import com.zespolowka.entity.createTest.TaskClosed;
import com.zespolowka.entity.createTest.TaskProgramming;
import com.zespolowka.entity.createTest.TaskProgrammingDetail;
import com.zespolowka.entity.solutionTest.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class SolutionTaskForm {
    public static final int CLOSEDTASK = 0;
    public static final int OPENTASK = 1;
    public static final int PROGRAMMINGTASK = 2;
    public static final int SQLTASK = 3;
    private Task task;
    private Map<String, Boolean> answers = new LinkedHashMap<>();
    private String answer = "";
    private String answerCode = "";
    private Set<String> languages = new TreeSet<>();
    private String language = "";
    private int taskType;

    public SolutionTaskForm(TaskSolution taskSolution) {
        this.task = taskSolution.getTask();
        if (taskSolution instanceof TaskClosedSolution) {
            this.taskType = CLOSEDTASK;
            this.answers = ((TaskClosedSolution) taskSolution).getAnswers();

        } else if (taskSolution instanceof TaskOpenSolution) {
            this.taskType = OPENTASK;
            this.answer = ((TaskOpenSolution) taskSolution).getAnswer();

        } else if (taskSolution instanceof TaskProgrammingSolution) {
            this.taskType = PROGRAMMINGTASK;
            this.language = ((TaskProgrammingSolution) taskSolution).getLanguage();
            this.answerCode = ((TaskProgrammingSolution) taskSolution).getAnswerCode();
            TaskProgramming taskProgramming = (TaskProgramming) task;
            Set<TaskProgrammingDetail> taskProgrammingDetails = taskProgramming.getProgrammingDetailSet();
            this.languages.addAll(taskProgrammingDetails.stream()
                    .map(taskProgrammingDetail -> taskProgrammingDetail.getLanguage().toString())
                    .collect(Collectors.toList()));

        } else if (taskSolution instanceof TaskSqlSolution) {
            this.taskType = SQLTASK;
            this.answerCode = ((TaskSqlSolution) taskSolution).getSqlAnswer();
        }
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
            languages.addAll(taskProgrammingDetails.stream()
                    .map(taskProgrammingDetail -> taskProgrammingDetail.getLanguage().toString())
                    .collect(Collectors.toList()));
        }

    }
}
