package com.zespolowka.entity.createTest;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tasks")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
public abstract class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "idTask")
    private Long id;
    @Lob
    @Column(length = 10000)
    private String question;
    private Float max_points;

    public Task(final String question, final Float max_points) {
        this.question = question;
        this.max_points = max_points;
    }

}
