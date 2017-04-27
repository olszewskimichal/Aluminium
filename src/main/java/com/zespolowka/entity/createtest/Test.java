package com.zespolowka.entity.createtest;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Entity
@javax.persistence.Table(name = "TestTable")
@Data
public class Test {
	@GeneratedValue
	@Id
	@Column(name = "idTest")
	private Long id;
	private String name;
	@Lob
	@Column(length = 1000)
	private String messageFAQ = "";
	private Long attempts;
	private LocalDate beginDate;
	private LocalDate endDate;
	private Float maxPoints;
	private String passHash = "";
	private Integer timePerAttempt;
	@OneToMany(cascade = CascadeType.ALL)
	private List<Task> tasks;


	public Test() {
		this.tasks = new ArrayList<>();
		this.maxPoints = 0.0f;
		this.passHash = "";
	}

	public Test(final String name, final Long attempts, final LocalDate beginDate, final LocalDate endDate, final List<Task> tasks, final String messageFAQ) {
		this.name = name;
		this.attempts = attempts;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.tasks = tasks;
		this.maxPoints = 0.0f;
		this.messageFAQ = messageFAQ;
	}

	public void addTaskToTest(final Task task) {
		tasks.add(task);
		updateMaxPoints(task.getMaxPoints());
	}

	public Boolean isOpenTest() {
		return passHash.length() <= 0;
	}

	public void updateMaxPoints(Float points) {
		this.maxPoints += points;
	}

}
