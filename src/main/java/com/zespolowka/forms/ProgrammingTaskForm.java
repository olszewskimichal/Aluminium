package com.zespolowka.forms;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Admin on 2016-04-12.
 */
@Data
@NoArgsConstructor
public class ProgrammingTaskForm implements Comparable<ProgrammingTaskForm> {
    private String language;
    private String restrictedList;
    private String testCode;
    private String testClassName;
    private String solutionClassName;
    private Boolean hidden = false;

    public ProgrammingTaskForm(String language) {
        this.language = language;
        this.hidden = false;
    }

    public ProgrammingTaskForm(String language, Boolean hidden) {
        this.language = language;
        this.hidden = hidden;
    }

    @Override
    public int compareTo(ProgrammingTaskForm o) {
        return this.language.compareTo(o.language);
    }
}
