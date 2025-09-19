package com.example.SpringToDo.controller;

import com.example.SpringToDo.dto.TaskCreateDTO;
import com.example.SpringToDo.dto.TaskDTO;
import com.example.SpringToDo.exception.GlobalExceptionHandler;
import com.example.SpringToDo.exception.TaskNotFoundException;
import com.example.SpringToDo.model.TaskStatus;
import com.example.SpringToDo.service.TaskService;
import com.example.SpringToDo.testfactory.TaskTestFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private com.example.SpringToDo.mapper.TaskMapper taskMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/tasks - успешно создать задачу")
    void createTask_success() throws Exception {
        TaskCreateDTO dto = TaskTestFactory.createDefaultTaskCreateDTO();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(taskService).createTask(any(TaskCreateDTO.class));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - успешно получить задачу по ID")
    void getTaskById_success() throws Exception {
        TaskDTO dto = TaskTestFactory.createDefaultTaskDTO();

        when(taskService.getTaskById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/tasks/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()))
                .andExpect(jsonPath("$.title").value(dto.getTitle()))
                .andExpect(jsonPath("$.status").value(dto.getStatus().toString()));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - задача не найдена возвращает 404")
    void getTaskById_notFound() throws Exception {
        when(taskService.getTaskById(999)).thenThrow(new TaskNotFoundException(999));

        mockMvc.perform(get("/api/v1/tasks/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task with id 999 not found"));
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} - успешно обновить задачу")
    void updateTask_success() throws Exception {
        TaskDTO dto = TaskTestFactory.createDefaultTaskDTO();

        when(taskService.updateTask(eq(dto.getId()), any(TaskDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/tasks/{id}", dto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId()));

        verify(taskService).updateTask(eq(dto.getId()), any(TaskDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - успешно удалить задачу")
    void deleteTask_success() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/{id}", 1))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(eq(1));
    }

    @Test
    @DisplayName("GET /api/v1/tasks - получить все задачи")
    void getAllTasks_success() throws Exception {
        List<TaskDTO> dtos = List.of(
                TaskTestFactory.createDefaultTaskDTO(),
                TaskTestFactory.createDefaultTaskDTO()
        );

        when(taskService.getAllTasks()).thenReturn(dtos);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(dtos.size()))
                .andExpect(jsonPath("$[0].title").value(dtos.get(0).getTitle()))
                .andExpect(jsonPath("$[1].title").value(dtos.get(1).getTitle()));
    }

    @Test
    @DisplayName("GET /api/v1/tasks?status=TODO - фильтрация по статусу")
    void getTasks_filteredByStatus() throws Exception {
        List<TaskDTO> todoDtos = List.of(TaskTestFactory.createDefaultTaskDTO());

        when(taskService.filterTasksByStatus(TaskStatus.TODO)).thenReturn(todoDtos);

        mockMvc.perform(get("/api/v1/tasks").param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(todoDtos.size()))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks?sort=dueDate - сортировка по дате")
    void getTasks_sortedByDueDate() throws Exception {
        when(taskService.getAllTasksSortedByDueDate()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/tasks").param("sort", "dueDate"))
                .andExpect(status().isOk());
    }
}
