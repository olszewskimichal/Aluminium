package com.zespolowka.entity.createTest;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class TaskProgrammingDetail {
    @Id
    @GeneratedValue
    private Long id;
    @Lob
    @Column(length = 10000)
    private String testCode;
    @Lob
    @Column(length = 10000)
    private String restrictedList;
    @Enumerated(EnumType.STRING)
    private ProgrammingLanguages language;
    private String testClassName;
    private String solutionClassName;

}


