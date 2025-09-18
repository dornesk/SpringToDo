package com.example.SpringToDo.dto;

import com.example.SpringToDo.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private int id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
}

