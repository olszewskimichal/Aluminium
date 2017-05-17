package com.zespolowka.entity.solution.test;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

import com.zespolowka.entity.createtest.Task;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public abstract class TaskSolution {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Getter
	@Setter
	private BigDecimal points;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "taskId")
	@Setter
	@Getter
	private Task task;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "solTestId")
	@Setter
	private SolutionTest solutionTest;


	public TaskSolution(Task task) {
		this.task = task;
	}
}
