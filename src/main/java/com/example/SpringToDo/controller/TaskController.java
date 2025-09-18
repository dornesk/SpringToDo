package com.example.SpringToDo.controller;

import com.example.SpringToDo.model.Task;
import com.example.SpringToDo.model.TaskStatus;
import com.example.SpringToDo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Void> createTask(@RequestBody Task task) {
        taskService.createTask(task);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable int id, @RequestBody Task task) {
        task.setId(id);
        taskService.updateTask(task);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable int id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable int id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String sort) {
        List<Task> tasks;
        if (status != null) {
            tasks = taskService.filterTasksByStatus(status);//если есть статус — фильтруем по нему
        } else if ("dueDate".equalsIgnoreCase(sort)) {
            tasks = taskService.getAllTasksSortedByDueDate();//сортируем по дате
        } else if ("status".equalsIgnoreCase(sort)) {
            tasks = taskService.getAllTasksSortedByStatus();//сортируем по статусу
        } else {
            tasks = taskService.getAllTasks();//иначе - возвращаем все задачи
        }
        return ResponseEntity.ok(tasks);
    }
}
