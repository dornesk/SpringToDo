package com.example.SpringToDo.testfactory;

import com.example.SpringToDo.dto.TaskCreateDTO;
import com.example.SpringToDo.dto.TaskDTO;
import com.example.SpringToDo.model.TaskStatus;

import java.time.LocalDate;

public class TaskTestFactory {

    public static TaskCreateDTO createDefaultTaskCreateDTO() {
        return new TaskCreateDTO(
                "Default Title",
                "Default Description",
                LocalDate.now().plusDays(1),
                TaskStatus.TODO
        );
    }

    public static TaskDTO createDefaultTaskDTO() {
        return new TaskDTO(
                1,
                "Default Title",
                "Default Description",
                LocalDate.now().plusDays(1),
                TaskStatus.TODO
        );
    }
}
