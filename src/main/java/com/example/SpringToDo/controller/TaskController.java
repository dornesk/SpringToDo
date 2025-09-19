package com.example.SpringToDo.controller;

import com.example.SpringToDo.dto.TaskCreateDTO;
import com.example.SpringToDo.dto.TaskDTO;
import com.example.SpringToDo.mapper.TaskMapper;
import com.example.SpringToDo.model.Task;
import com.example.SpringToDo.model.TaskStatus;
import com.example.SpringToDo.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @PostMapping
    public ResponseEntity<Void> createTask(@RequestBody TaskCreateDTO dto) {
        Task task = taskMapper.toEntity(dto);
        taskService.createTask(task);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@PathVariable int id, @RequestBody TaskDTO dto) {
        Task task = taskMapper.toEntity(dto);
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
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable int id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(taskMapper.toDto(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String sort) {
        List<Task> tasks;
        if (status != null) {
            tasks = taskService.filterTasksByStatus(status);
        } else if ("dueDate".equalsIgnoreCase(sort)) {
            tasks = taskService.getAllTasksSortedByDueDate();
        } else if ("status".equalsIgnoreCase(sort)) {
            tasks = taskService.getAllTasksSortedByStatus();
        } else {
            tasks = taskService.getAllTasks();
        }
        List<TaskDTO> dtos = tasks.stream().map(taskMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
