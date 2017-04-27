package com.zespolowka.entity.solution.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CompilationError {
	@Id
	@GeneratedValue
	private Long id;
	@Enumerated(EnumType.STRING)
	private CompilationErrorTypes type;
	@Lob
	@Column(length = 100000)
	private String error;

}