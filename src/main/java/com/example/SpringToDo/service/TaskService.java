package com.example.SpringToDo.service;

import com.example.SpringToDo.model.Task;
import com.example.SpringToDo.model.TaskStatus;

import java.util.List;

public interface TaskService {
    void createTask(Task task);
    void updateTask(Task task);
    void deleteTask(int id);

    List<Task> getAllTasks();
    Task getTaskById(int id);
    List<Task> filterTasksByStatus(TaskStatus status);
    List<Task> getAllTasksSortedByDueDate();
    List<Task> getAllTasksSortedByStatus();
}
