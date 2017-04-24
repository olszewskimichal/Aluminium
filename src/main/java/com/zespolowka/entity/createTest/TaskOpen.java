package com.zespolowka.entity.createTest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TaskOpen extends Task {
    @Lob
    @Column(length = 10000)
    private String answer = "";

    private Boolean caseSens;

    public TaskOpen(String question, Float max_points) {
        super(question, max_points);
        this.answer = "";
        this.caseSens = false;
    }

}
