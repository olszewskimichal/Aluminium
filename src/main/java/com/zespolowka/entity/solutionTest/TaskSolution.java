package com.zespolowka.entity.solutionTest;

import com.zespolowka.entity.createTest.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
public class TaskSolution {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private Float points;

    @ManyToOne(cascade = CascadeType.ALL)
    private Task task;


    public TaskSolution(Task task) {
        this.task = task;
    }
}
