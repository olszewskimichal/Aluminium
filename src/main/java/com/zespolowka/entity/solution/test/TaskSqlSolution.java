package com.zespolowka.entity.solution.test;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import com.zespolowka.entity.createtest.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskSqlSolution extends TaskSolution {
	@Lob
	@Column(length = 1000)
	private String sqlAnswer;

	@OneToOne(targetEntity = CompilationError.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private CompilationError compilationError;

	public TaskSqlSolution(Task task) {
		super(task);
		this.sqlAnswer = "";
	}

}
