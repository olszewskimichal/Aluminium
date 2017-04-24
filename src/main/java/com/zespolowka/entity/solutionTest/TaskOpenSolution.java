package com.zespolowka.entity.solutionTest;

import com.zespolowka.entity.createTest.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskOpenSolution extends TaskSolution {
    @Lob
    @Column(length = 10000)
    private String answer = "";


    public TaskOpenSolution(Task task) {
        super(task);
        this.answer = "";
    }

}
