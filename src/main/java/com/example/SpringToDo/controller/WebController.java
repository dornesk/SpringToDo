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
        List<TaskDTO> dtos;
        if ("dueDate".equalsIgnoreCase(sort)) {
            dtos = taskService.getAllTasksSortedByDueDate();
        } else if ("status".equalsIgnoreCase(sort)) {
            dtos = taskService.getAllTasksSortedByStatus();
        } else {
            dtos = taskService.getAllTasks();
        }
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
        taskService.createTask(taskCreateDTO);
        return "redirect:/";
    }

    @GetMapping("/tasks/{id}")
    public String getTaskPage(@PathVariable int id, Model model) {
        try {
            TaskDTO dto = taskService.getTaskById(id);
            model.addAttribute("task", dto);
            return "taskDetails";
        } catch (NoSuchElementException ex) {
            return "taskNotFound";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        try {
            TaskDTO dto = taskService.getTaskById(id);
            model.addAttribute("task", dto);
            model.addAttribute("statuses", TaskStatus.values());
            return "editTask"; // новый шаблон с формой редактирования
        } catch (NoSuchElementException e) {
            return "taskNotFound";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateTask(@PathVariable int id, @ModelAttribute("task") TaskDTO taskDTO) {
        taskService.updateTask(id, taskDTO);
        return "redirect:/";
    }

}
