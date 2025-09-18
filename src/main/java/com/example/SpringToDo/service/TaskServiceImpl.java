package com.example.SpringToDo.service;

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
    public void createTask(Task task) {
        validateTask(task);
        repository.save(task);
    }

    @Override
    public void updateTask(Task task) {
        validateTask(task);
        if (!repository.existsById(task.getId())) {
            throw new NoSuchElementException("Task with this ID not found");
        }
        repository.save(task);
    }

    @Override
    public void deleteTask(int id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Task with this ID not found");
        }
        repository.deleteById(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    @Override
    public Task getTaskById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task with this ID not found"));
    }

    @Override
    public List<Task> filterTasksByStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        return repository.findAll().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getAllTasksSortedByDueDate() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getAllTasksSortedByStatus() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Task::getStatus))
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
