package com.zespolowka.entity.solution.test;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

import com.zespolowka.entity.createtest.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@NoArgsConstructor
public abstract class TaskSolution {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private BigDecimal points;

	@ManyToOne(cascade = CascadeType.ALL)
	private Task task;


	public TaskSolution(Task task) {
		this.task = task;
	}
}
