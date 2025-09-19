package com.example.SpringToDo.service;

import com.example.SpringToDo.dto.TaskCreateDTO;
import com.example.SpringToDo.dto.TaskDTO;
import com.example.SpringToDo.exception.TaskNotFoundException;
import com.example.SpringToDo.mapper.TaskMapper;
import com.example.SpringToDo.model.Task;
import com.example.SpringToDo.model.TaskStatus;
import com.example.SpringToDo.repository.TaskRepository;
import com.example.SpringToDo.testfactory.TaskTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {
    private TaskRepository repository;
    private TaskMapper taskMapper;
    private TaskServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(TaskRepository.class);
        taskMapper = mock(TaskMapper.class);
        service = new TaskServiceImpl(repository, taskMapper);

        when(taskMapper.toEntity(any(TaskCreateDTO.class))).thenAnswer(i -> {
            TaskCreateDTO dto = i.getArgument(0);
            return new Task(0, dto.getTitle(), dto.getDescription(), dto.getDueDate(), dto.getStatus());
        });

        when(taskMapper.toEntity(any(TaskDTO.class))).thenAnswer(i -> {
            TaskDTO dto = i.getArgument(0);
            return new Task(dto.getId(), dto.getTitle(), dto.getDescription(), dto.getDueDate(), dto.getStatus());
        });

        when(taskMapper.toDto(any(Task.class))).thenAnswer(i -> {
            Task task = i.getArgument(0);
            return new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getStatus());
        });

        when(taskMapper.toDtoList(anyList())).thenAnswer(i -> {
            List<Task> tasks = i.getArgument(0);
            return tasks.stream()
                    .map(task -> new TaskDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getStatus()))
                    .toList();
        });
    }

    @Test
    @DisplayName("Успешное создание задачи вызывает save и возвращает DTO")
    void createTask_shouldSaveAndReturnDTO() {
        TaskCreateDTO dto = TaskTestFactory.createDefaultTaskCreateDTO();
        Task taskEntity = new Task(0, dto.getTitle(), dto.getDescription(), dto.getDueDate(), dto.getStatus());
        Task savedEntity = new Task(1, dto.getTitle(), dto.getDescription(), dto.getDueDate(), dto.getStatus());

        when(repository.save(any(Task.class))).thenReturn(savedEntity);

        TaskDTO result = service.createTask(dto);

        verify(repository).save(taskEntity);
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals(savedEntity.getTitle(), result.getTitle());
    }

    @Test
    @DisplayName("Обновление существующей задачи вызывает save и возвращает DTO")
    void updateTask_existingId_shouldSaveAndReturnDTO() {
        TaskDTO dto = TaskTestFactory.createDefaultTaskDTO();
        when(repository.existsById(dto.getId())).thenReturn(true);
        when(repository.save(any(Task.class))).thenReturn(new Task(dto.getId(), dto.getTitle(), dto.getDescription(), dto.getDueDate(), dto.getStatus()));

        TaskDTO updated = service.updateTask(dto.getId(), dto);

        verify(repository).existsById(dto.getId());
        verify(repository).save(any(Task.class));
        assertEquals(dto.getId(), updated.getId());
    }

    @Test
    @DisplayName("Обновление задачи с несуществующим ID вызывает TaskNotFoundException")
    void updateTask_nonExistentId_shouldThrow() {
        TaskDTO dto = TaskTestFactory.createDefaultTaskDTO();
        when(repository.existsById(dto.getId())).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> service.updateTask(dto.getId(), dto));
        verify(repository).existsById(dto.getId());
        verify(repository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Удаление задачи с существующим ID вызывает deleteById")
    void deleteTask_existingId_shouldDelete() {
        int id = 1;
        when(repository.existsById(id)).thenReturn(true);

        service.deleteTask(id);

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Удаление задачи с несуществующим ID вызывает TaskNotFoundException")
    void deleteTask_nonExistentId_shouldThrow() {
        int id = 999;
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> service.deleteTask(id));
        verify(repository).existsById(id);
        verify(repository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Получение всех задач возвращает список DTO")
    void getAllTasks_shouldReturnDtoList() {
        List<Task> tasks = List.of(
                new Task(1, "T1", "D1", LocalDate.now().plusDays(1), TaskStatus.TODO),
                new Task(2, "T2", "D2", LocalDate.now().plusDays(2), TaskStatus.DONE)
        );
        when(repository.findAll()).thenReturn(tasks);

        List<TaskDTO> dtos = service.getAllTasks();

        assertEquals(tasks.size(), dtos.size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Получение задачи по существующему ID возвращает DTO")
    void getTaskById_existingId_shouldReturnDto() {
        Task task = new Task(1, "T1", "D1", LocalDate.now().plusDays(1), TaskStatus.TODO);
        when(repository.findById(1)).thenReturn(Optional.of(task));

        TaskDTO dto = service.getTaskById(1);

        assertEquals(task.getId(), dto.getId());
        verify(repository).findById(1);
    }

    @Test
    @DisplayName("Получение задачи по несуществующему ID вызывает TaskNotFoundException")
    void getTaskById_nonExistentId_shouldThrow() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.getTaskById(999));
    }

    @Test
    @DisplayName("Фильтрация по статусу возвращает отфильтрованные DTO")
    void filterTasksByStatus_validStatus_shouldReturnFiltered() {
        TaskStatus status = TaskStatus.TODO;
        Task t1 = new Task(1, "T1", "D1", LocalDate.now().plusDays(1), status);
        Task t2 = new Task(2, "T2", "D2", LocalDate.now().plusDays(1), TaskStatus.DONE);

        when(repository.findAll()).thenReturn(List.of(t1, t2));

        List<TaskDTO> filtered = service.filterTasksByStatus(status);

        assertEquals(1, filtered.size());
        assertEquals(status, filtered.get(0).getStatus());
    }

    @Test
    @DisplayName("Фильтрация по null статусу вызывает IllegalArgumentException")
    void filterTasksByStatus_null_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.filterTasksByStatus(null));
    }
}
