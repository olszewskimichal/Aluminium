package com.zespolowka.entity.solution.test;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

import com.zespolowka.entity.createtest.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TaskClosedSolution extends TaskSolution {
	@ElementCollection
	private Map<String, Boolean> answers = new LinkedHashMap<>();

	public TaskClosedSolution(Task task) {
		super(task);
	}

}
