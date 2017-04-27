package com.zespolowka.entity.solution.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import com.zespolowka.entity.createtest.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskOpenSolution extends TaskSolution {
	@Lob
	@Column(length = 10000)
	private String answer = "";


	public TaskOpenSolution(Task task) {
		super(task);
		this.answer = "";
	}

}
