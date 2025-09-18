package com.example.SpringToDo.controller;

import com.example.SpringToDo.model.Task;
import com.example.SpringToDo.model.TaskStatus;
import com.example.SpringToDo.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@AllArgsConstructor
public class WebController {
    private final TaskService taskService;

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
        model.addAttribute("tasks", tasks);
        return "index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("statuses", TaskStatus.values());
        return "createTask";
    }

    @PostMapping("/create")
    public String createTaskSubmit(@ModelAttribute Task task) {
        taskService.createTask(task);
        return "redirect:/";  // после создания перенаправляем на главную
    }

    @GetMapping("/tasks/{id}")
    public String getTaskPage(@PathVariable int id, Model model) {
        try {
            Task task = taskService.getTaskById(id);
            model.addAttribute("task", task);
            return "taskDetails";  // шаблон taskDetails.html для отображения задачи
        } catch (NoSuchElementException ex) {
            return "taskNotFound"; // шаблон с сообщением, что задача не найдена
        }
    }

}
