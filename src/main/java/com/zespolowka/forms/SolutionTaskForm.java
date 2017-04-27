package com.zespolowka.forms;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.zespolowka.entity.createtest.Task;
import com.zespolowka.entity.createtest.TaskClosed;
import com.zespolowka.entity.createtest.TaskProgramming;
import com.zespolowka.entity.createtest.TaskProgrammingDetail;
import com.zespolowka.entity.solution.test.TaskClosedSolution;
import com.zespolowka.entity.solution.test.TaskOpenSolution;
import com.zespolowka.entity.solution.test.TaskProgrammingSolution;
import com.zespolowka.entity.solution.test.TaskSolution;
import com.zespolowka.entity.solution.test.TaskSqlSolution;
import lombok.Data;
import lombok.NoArgsConstructor;

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

		}
		else if (taskSolution instanceof TaskOpenSolution) {
			this.taskType = OPENTASK;
			this.answer = ((TaskOpenSolution) taskSolution).getAnswer();

		}
		else if (taskSolution instanceof TaskProgrammingSolution) {
			this.taskType = PROGRAMMINGTASK;
			this.language = ((TaskProgrammingSolution) taskSolution).getLanguage();
			this.answerCode = ((TaskProgrammingSolution) taskSolution).getAnswerCode();
			TaskProgramming taskProgramming = (TaskProgramming) task;
			Set<TaskProgrammingDetail> taskProgrammingDetails = taskProgramming.getProgrammingDetailSet();
			this.languages.addAll(taskProgrammingDetails.stream().map(taskProgrammingDetail -> taskProgrammingDetail.getLanguage().toString()).collect(Collectors.toList()));

		}
		else if (taskSolution instanceof TaskSqlSolution) {
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
			languages.addAll(taskProgrammingDetails.stream().map(taskProgrammingDetail -> taskProgrammingDetail.getLanguage().toString()).collect(Collectors.toList()));
		}

	}
}
