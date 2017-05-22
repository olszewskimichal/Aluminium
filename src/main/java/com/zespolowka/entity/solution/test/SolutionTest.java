package com.zespolowka.entity.solution.test;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zespolowka.entity.createtest.Test;
import com.zespolowka.entity.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SolutionTest {
	@Id
	@GeneratedValue
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	private Test test;
	private Integer attempt;
	private LocalDateTime beginSolution;
	private LocalDateTime endSolution;
	private BigDecimal points;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")

	private User user;
	@Enumerated(EnumType.STRING)

	private SolutionStatus solutionStatus;

	@OneToMany(mappedBy = "solutionTest", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TaskSolution> solutionTasks;


	public SolutionTest(Test test, User user) {
		this.solutionTasks = new ArrayList<>();
		this.solutionStatus = SolutionStatus.OPEN;
		this.test = test;
		this.points = BigDecimal.ZERO;
		this.user = user;
	}

	public Long secondsToEnd() {
		Timestamp timestamp = Timestamp.valueOf(beginSolution.plusMinutes(test.getTimePerAttempt()));
		Timestamp timestamp1 = Timestamp.valueOf(LocalDateTime.now());
		Long value = timestamp.getTime() - timestamp1.getTime();
		return value / 1000L;
	}

	public void addSolutionTask(TaskSolution solution) {
		this.solutionTasks.add(solution);
		solution.setSolutionTest(this);
	}

	public void removeSolutionTask(TaskSolution solution) {
		solutionTasks.remove(solution);
		solution.setSolutionTest(null);
	}

	public List<TaskSolution> getSolutionTasks() {
		return Collections.unmodifiableList(solutionTasks);
	}
}
