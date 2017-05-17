package com.zespolowka.entity.createtest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TaskOpen extends Task {
	@Lob
	@Column(length = 10000)
	private String answer = "";

	private Boolean caseSens;

	public TaskOpen(String question, BigDecimal maxPoints) {
		super(question, maxPoints);
		this.answer = "";
		this.caseSens = false;
	}

}
