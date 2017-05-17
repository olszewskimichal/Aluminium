package com.zespolowka.entity.createtest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
public abstract class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "idTask")
	private Long id;
	@Lob
	@Column(length = 10000)
	private String question;
	private BigDecimal maxPoints;

	public Task(final String question, final BigDecimal maxPoints) {
		this.question = question;
		this.maxPoints = maxPoints;
	}

}
