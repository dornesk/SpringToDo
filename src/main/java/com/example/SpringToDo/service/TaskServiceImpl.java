package com.example.SpringToDo.service;

import com.example.SpringToDo.dto.TaskCreateDTO;
import com.example.SpringToDo.dto.TaskDTO;
import com.example.SpringToDo.exception.TaskNotFoundException;
import com.example.SpringToDo.mapper.TaskMapper;
import com.example.SpringToDo.model.Task;
import com.example.SpringToDo.model.TaskStatus;
import com.example.SpringToDo.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository repository;
    private final TaskMapper taskMapper;

    @Override
    public TaskDTO createTask(TaskCreateDTO dto) {
        Task task = taskMapper.toEntity(dto);
        validateTask(task);
        Task saved = repository.save(task);
        return taskMapper.toDto(saved);
    }

    @Override
    public TaskDTO updateTask(int id, TaskDTO dto) {
        Task task = taskMapper.toEntity(dto);
        task.setId(id);
        validateTask(task);
        if (!repository.existsById(task.getId())) {
            throw new TaskNotFoundException(id);
        }
        repository.save(task);
        return dto;
    }

    @Override
    public void deleteTask(int id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = repository.findAll();
        tasks.forEach(t -> System.out.println("Entity task: " + t));
        List<TaskDTO> dtos = taskMapper.toDtoList(tasks);
        dtos.forEach(d -> System.out.println("DTO: " + d));
        return dtos;
    }

    @Override
    public TaskDTO getTaskById(int id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return taskMapper.toDto(task);
    }

    @Override
    public List<TaskDTO> filterTasksByStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        List<Task> tasks = repository.findAll().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getAllTasksSortedByDueDate() {
        List<Task> tasks = repository.findAll().stream()
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());

        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getAllTasksSortedByStatus() {
        List<Task> tasks = repository.findAll().stream()
                .sorted(Comparator.comparing(Task::getStatus))
                .collect(Collectors.toList());

        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateTask(Task task) {
        if (task == null) {
            throw new NullPointerException("Task cannot be null");
        }
        if (task.getDueDate() == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }
        if (task.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (task.getStatus() == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }
}
