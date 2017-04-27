package com.zespolowka.entity.createtest;

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
public class TaskProgrammingDetail {
	@Id
	@GeneratedValue
	private Long id;
	@Lob
	@Column(length = 10000)
	private String testCode;
	@Lob
	@Column(length = 10000)
	private String restrictedList;
	@Enumerated(EnumType.STRING)
	private ProgrammingLanguages language;
	private String testClassName;
	private String solutionClassName;

}


