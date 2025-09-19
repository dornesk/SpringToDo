package com.example.SpringToDo.service;

import com.example.SpringToDo.dto.TaskCreateDTO;
import com.example.SpringToDo.dto.TaskDTO;
import com.example.SpringToDo.model.Task;
import com.example.SpringToDo.model.TaskStatus;

import java.util.List;

public interface TaskService {
    void createTask(TaskCreateDTO dto);
    void updateTask(int id, TaskDTO dto);
    void deleteTask(int id);

    List<TaskDTO> getAllTasks();
    TaskDTO getTaskById(int id);
    List<TaskDTO> filterTasksByStatus(TaskStatus status);
    List<TaskDTO> getAllTasksSortedByDueDate();
    List<TaskDTO> getAllTasksSortedByStatus();
}
