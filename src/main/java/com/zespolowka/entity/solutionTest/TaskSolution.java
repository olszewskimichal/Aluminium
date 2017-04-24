package com.zespolowka.entity.solutionTest;

import com.zespolowka.entity.createTest.Task;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TaskSolution {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private Float points;

    @ManyToOne(cascade = CascadeType.ALL)
    private Task task;

    public TaskSolution() {
    }

    public TaskSolution(Task task) {
        this.task = task;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getPoints() {
        return points;
    }

    public void setPoints(Float points) {
        this.points = points;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "TaskSolution{" +
                "id=" + id +
                ", points=" + points +
                ", task=" + task +
                '}';
    }
}
