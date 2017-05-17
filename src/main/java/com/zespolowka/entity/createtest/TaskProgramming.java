package com.zespolowka.entity.createtest;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskProgramming extends Task {

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = TaskProgrammingDetail.class, fetch = FetchType.EAGER)
	private Set<TaskProgrammingDetail> programmingDetailSet = new HashSet<>();


	public TaskProgramming(final String question, final BigDecimal maxPoints) {
		super(question, maxPoints);
	}

	public void addTaskProgrammingDetail(TaskProgrammingDetail taskProgrammingDetail) {
		this.programmingDetailSet.add(taskProgrammingDetail);
	}

}
