package com.zespolowka.forms;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SolutionTestForm {
    private String name;

    private Long solutionId;

    private List<SolutionTaskForm> tasks;

}
