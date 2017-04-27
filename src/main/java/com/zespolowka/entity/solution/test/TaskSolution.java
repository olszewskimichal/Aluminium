package com.zespolowka.entity.solution.test;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import com.zespolowka.entity.createtest.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
public class TaskSolution {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	private Float points;

	@ManyToOne(cascade = CascadeType.ALL)
	private Task task;


	public TaskSolution(Task task) {
		this.task = task;
	}
}
