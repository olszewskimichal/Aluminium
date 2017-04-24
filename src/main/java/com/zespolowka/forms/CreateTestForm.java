package com.zespolowka.forms;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
public class CreateTestForm {

    @Size(min = 5, max = 30)
    private String name;

    private String messageFAQ;

    @Min(1)
    private Integer attempts = 1;

    @NotNull
    private String beginDate = LocalDate.now().toString();

    @NotNull
    private String endDate = LocalDate.now().plusWeeks(1L).toString();

    @Min(5)
    private int timePerAttempt = 5;

    private String password = "";

    @NotEmpty
    @Valid
    private List<TaskForm> tasks;

    public CreateTestForm() {
        this.attempts = 1;
        this.timePerAttempt = 5;
        this.tasks = new ArrayList<>();
    }

    public void addTask(TaskForm task) {
        this.tasks.add(task);
    }
}
