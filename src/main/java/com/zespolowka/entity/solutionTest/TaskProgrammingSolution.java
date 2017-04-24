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
public class TaskProgrammingSolution extends TaskSolution {
    @Lob
    @Column(length = 10000)
    private String answerCode;

    private String language;

    @Lob
    @Column(length = 10000)
    private String failedUnitTest;

    @OneToOne(targetEntity = CompilationError.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private CompilationError compilationError;

    public TaskProgrammingSolution(Task task) {
        super(task);
        this.answerCode = "";
    }

}
