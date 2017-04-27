package com.zespolowka.forms;


import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SolutionTestForm {
	private String name;

	private Long solutionId;

	private List<SolutionTaskForm> tasks;

}
