package com.zespolowka.entity.solutionTest;

import com.zespolowka.entity.createTest.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TaskClosedSolution extends TaskSolution {
    @ElementCollection
    private Map<String, Boolean> answers = new LinkedHashMap<>();

    public TaskClosedSolution(Task task) {
        super(task);
    }

}
