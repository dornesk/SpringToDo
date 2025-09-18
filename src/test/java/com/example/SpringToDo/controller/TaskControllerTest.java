package com.example.SpringToDo.controller;

import com.example.SpringToDo.model.Task;
import com.example.SpringToDo.model.TaskStatus;
import com.example.SpringToDo.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class TaskControllerTest {
    private final MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/tasks - успешно создать задачу")
    void createTask_success() throws Exception {
        Task task = new Task(0, "Title", "Desc", LocalDate.now().plusDays(1), TaskStatus.TODO);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated());

        Mockito.verify(taskService).createTask(any(Task.class));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - успешно получить задачу по ID")
    void getTaskById_success() throws Exception {
        Task task = new Task(1, "Title", "Desc", LocalDate.now().plusDays(1), TaskStatus.TODO);

        Mockito.when(taskService.getTaskById(1)).thenReturn(task);

        mockMvc.perform(get("/api/v1/tasks/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - задача не найдена возвращает 404")
    void getTaskById_notFound() throws Exception {
        Mockito.when(taskService.getTaskById(999)).thenThrow(new NoSuchElementException("Task with this ID not found"));

        mockMvc.perform(get("/api/v1/tasks/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task with this ID not found"));
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} - успешно обновить задачу")
    void updateTask_success() throws Exception {
        Task updatedTask = new Task(1, "Updated", "Desc", LocalDate.now().plusDays(2), TaskStatus.IN_PROGRESS);

        mockMvc.perform(put("/api/v1/tasks/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk());

        Mockito.verify(taskService).updateTask(any(Task.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - успешно удалить задачу")
    void deleteTask_success() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/{id}", 1))
                .andExpect(status().isNoContent());

        Mockito.verify(taskService).deleteTask(eq(1));
    }

    @Test
    @DisplayName("GET /api/v1/tasks - получить все задачи")
    void getAllTasks_success() throws Exception {
        List<Task> tasks = List.of(
                new Task(1, "T1", "D1", LocalDate.now().plusDays(1), TaskStatus.TODO),
                new Task(2, "T2", "D2", LocalDate.now().plusDays(2), TaskStatus.DONE)
        );

        Mockito.when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(tasks.size()))
                .andExpect(jsonPath("$[0].title").value("T1"))
                .andExpect(jsonPath("$[1].title").value("T2"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks?status=TODO - фильтрация по статусу")
    void getTasks_filteredByStatus() throws Exception {
        List<Task> todoTasks = List.of(
                new Task(1, "T1", "D1", LocalDate.now().plusDays(1), TaskStatus.TODO)
        );

        Mockito.when(taskService.filterTasksByStatus(TaskStatus.TODO)).thenReturn(todoTasks);

        mockMvc.perform(get("/api/v1/tasks").param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(todoTasks.size()))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks?sort=dueDate - сортировка по дате")
    void getTasks_sortedByDueDate() throws Exception {
        Mockito.when(taskService.getAllTasksSortedByDueDate()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/tasks").param("sort", "dueDate"))
                .andExpect(status().isOk());
    }
}
