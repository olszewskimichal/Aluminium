package com.zespolowka.entity.solutionTest;

import com.zespolowka.entity.createTest.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskSqlSolution extends TaskSolution {
    @Lob
    @Column(length = 1000)
    private String sqlAnswer;

    @OneToOne(targetEntity = CompilationError.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private CompilationError compilationError;

    public TaskSqlSolution(Task task) {
        super(task);
        this.sqlAnswer = "";
    }

}
