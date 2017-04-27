package com.zespolowka.entity.createtest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskSql extends Task {
	@Lob
	@Column(length = 1000)
	private String sqlAnswer;

	@Lob
	@Column(length = 10000)
	private String preparations;

	public TaskSql(String question, Float maxPoints) {
		super(question, maxPoints);
	}
}
