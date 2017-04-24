package com.zespolowka.entity.solutionTest;

import com.zespolowka.entity.createTest.Test;
import com.zespolowka.entity.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class SolutionTest {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(targetEntity = Test.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "idTest")
    private Test test;
    private Integer attempt;
    private LocalDateTime beginSolution;
    private LocalDateTime endSolution;
    private Float points;
    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private SolutionStatus solutionStatus;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TaskSolution> solutionTasks;


    public SolutionTest(Test test, User user) {
        this.solutionTasks = new ArrayList<>();
        this.solutionStatus = SolutionStatus.OPEN;
        this.test = test;
        this.points = 0.0f;
        this.user = user;
    }

    public Long secondsToEnd() {
        Timestamp timestamp = Timestamp.valueOf(beginSolution.plusMinutes(test.getTimePerAttempt()));
        Timestamp timestamp1 = Timestamp.valueOf(LocalDateTime.now());
        Long value = timestamp.getTime() - timestamp1.getTime();
        return value / 1000L;
    }
}
