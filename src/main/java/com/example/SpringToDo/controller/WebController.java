package com.example.SpringToDo.controller;

import com.example.SpringToDo.dto.TaskCreateDTO;
import com.example.SpringToDo.dto.TaskDTO;
import com.example.SpringToDo.mapper.TaskMapper;
import com.example.SpringToDo.model.Task;
import com.example.SpringToDo.model.TaskStatus;
import com.example.SpringToDo.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping("/")
    public String index(@RequestParam(required = false) String sort, Model model) {
        List<Task> tasks;
        if ("dueDate".equalsIgnoreCase(sort)) {
            tasks = taskService.getAllTasksSortedByDueDate();
        } else if ("status".equalsIgnoreCase(sort)) {
            tasks = taskService.getAllTasksSortedByStatus();
        } else {
            tasks = taskService.getAllTasks();
        }
        List<TaskDTO> dtos = tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
        model.addAttribute("tasks", dtos);
        return "index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new TaskCreateDTO());
        model.addAttribute("statuses", TaskStatus.values());
        return "createTask";
    }

    @PostMapping("/create")
    public String createTaskSubmit(@ModelAttribute TaskCreateDTO taskCreateDTO) {
        Task task = taskMapper.toEntity(taskCreateDTO);
        taskService.createTask(task);
        return "redirect:/";
    }

    @GetMapping("/tasks/{id}")
    public String getTaskPage(@PathVariable int id, Model model) {
        try {
            Task task = taskService.getTaskById(id);
            TaskDTO dto = taskMapper.toDto(task);
            model.addAttribute("task", dto);
            return "taskDetails";
        } catch (NoSuchElementException ex) {
            return "taskNotFound";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        try {
            Task task = taskService.getTaskById(id);
            TaskDTO dto = taskMapper.toDto(task);
            model.addAttribute("task", dto);
            model.addAttribute("statuses", TaskStatus.values());
            return "editTask"; // новый шаблон с формой редактирования
        } catch (NoSuchElementException e) {
            return "taskNotFound";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateTask(@PathVariable int id, @ModelAttribute("task") TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);
        task.setId(id);
        taskService.updateTask(task);
        return "redirect:/";
    }

}
