package com.zespolowka.entity.solution.test;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import com.zespolowka.entity.createtest.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskProgrammingSolution extends TaskSolution {
	@Lob
	@Column(length = 10000)
	private String answerCode;

	private String language;

	@Lob
	@Column(length = 10000)
	private String failedUnitTest;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	private CompilationError compilationError;

	public TaskProgrammingSolution(Task task) {
		super(task);
		this.answerCode = "";
	}

}
