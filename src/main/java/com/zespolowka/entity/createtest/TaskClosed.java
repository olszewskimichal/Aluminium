package com.zespolowka.entity.createtest;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Map;
import java.util.TreeMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TaskClosed extends Task {

	@Transient
	public static final int WRONG_RESET = 0;
	@Transient
	public static final int COUNT_NOT_FULL = 1;
	@ElementCollection
	private Map<String, Boolean> answers = new TreeMap<>();
	private int countingType;

	public TaskClosed(String question, Float maxPoints) {
		super(question, maxPoints);
	}

}
