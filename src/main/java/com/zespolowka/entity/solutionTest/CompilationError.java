package com.zespolowka.entity.solutionTest;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class CompilationError {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private CompilationErrorTypes type;
    @Lob
    @Column(length = 100000)
    private String Error;

}