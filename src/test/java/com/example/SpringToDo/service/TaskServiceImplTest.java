package com.example.SpringToDo.service;

import com.example.SpringToDo.model.Task;
import com.example.SpringToDo.model.TaskStatus;
import com.example.SpringToDo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {
    private TaskRepository repository;
    private TaskServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(TaskRepository.class);
        service = new TaskServiceImpl(repository);
    }

    @Test
    @DisplayName("Успешное создание задачи вызывает save")
    void createTask_shouldSaveTask() {
        Task task = new Task(0, "Title", "Desc", LocalDate.now().plusDays(1), TaskStatus.TODO);

        service.createTask(task);

        verify(repository, times(1)).save(task);
    }

    @Test
    @DisplayName("Создание задачи с null выбрасывает NullPointerException")
    void createTask_null_shouldThrow() {
        assertThrows(NullPointerException.class, () -> service.createTask(null));
    }

    @Test
    @DisplayName("Создание задачи с прошедшей датой выбрасывает IllegalArgumentException")
    void createTask_dueDateInPast_shouldThrow() {
        Task task = new Task(0, "Title", "Desc", LocalDate.now().minusDays(1), TaskStatus.TODO);
        assertThrows(IllegalArgumentException.class, () -> service.createTask(task));
    }

    @Test
    @DisplayName("Обновление существующей задачи вызывает save")
    void updateTask_existingTask_shouldSave() {
        Task task = new Task(1, "Updated", "Desc", LocalDate.now().plusDays(1), TaskStatus.IN_PROGRESS);

        when(repository.existsById(task.getId())).thenReturn(true);

        service.updateTask(task);

        verify(repository).existsById(task.getId());
        verify(repository).save(task);
    }

    @Test
    @DisplayName("Обновление задачи с несуществующим ID выбрасывает NoSuchElementException")
    void updateTask_nonExistentId_shouldThrow() {
        Task task = new Task(999, "Updated", "Desc", LocalDate.now().plusDays(1), TaskStatus.IN_PROGRESS);

        when(repository.existsById(task.getId())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.updateTask(task));
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
    @DisplayName("Удаление задачи с несуществующим ID выбрасывает NoSuchElementException")
    void deleteTask_nonExistentId_shouldThrow() {
        int id = 999;
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> service.deleteTask(id));
    }

    @Test
    @DisplayName("Получение всех задач возвращает список из репозитория")
    void getAllTasks_shouldReturnList() {
        List<Task> tasks = List.of(
                new Task(1, "T1", "D1", LocalDate.now().plusDays(1), TaskStatus.TODO),
                new Task(2, "T2", "D2", LocalDate.now().plusDays(2), TaskStatus.DONE)
        );
        when(repository.findAll()).thenReturn(tasks);

        List<Task> result = service.getAllTasks();

        assertEquals(tasks, result);
    }

    @Test
    @DisplayName("Получение задачи по ID существующего возвращает объект")
    void getTaskById_existingId_shouldReturnTask() {
        Task task = new Task(1, "T1", "D1", LocalDate.now().plusDays(1), TaskStatus.TODO);
        when(repository.findById(1)).thenReturn(Optional.of(task));

        Task result = service.getTaskById(1);

        assertEquals(task, result);
    }

    @Test
    @DisplayName("Получение задачи по несуществующему ID выбрасывает NoSuchElementException")
    void getTaskById_nonExistentId_shouldThrow() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getTaskById(999));
    }

    @Test
    @DisplayName("Фильтрация по статусу возвращает только нужные задачи")
    void filterTasksByStatus_validStatus_shouldReturnFiltered() {
        Task t1 = new Task(1, "T1", "D1", LocalDate.now().plusDays(1), TaskStatus.TODO);
        Task t2 = new Task(2, "T2", "D2", LocalDate.now().plusDays(1), TaskStatus.DONE);
        when(repository.findAll()).thenReturn(List.of(t1, t2));

        List<Task> filtered = service.filterTasksByStatus(TaskStatus.TODO);

        assertEquals(1, filtered.size());
        assertTrue(filtered.contains(t1));
        assertFalse(filtered.contains(t2));
    }

    @Test
    @DisplayName("Фильтрация по null статусу выбрасывает IllegalArgumentException")
    void filterTasksByStatus_null_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.filterTasksByStatus(null));
    }

    @Test
    @DisplayName("Сортировка задач по дате")
    void getAllTasksSortedByDueDate_shouldReturnSorted() {
        Task t1 = new Task(1, "T1", "D1", LocalDate.of(2025, 8, 10), TaskStatus.TODO);
        Task t2 = new Task(2, "T2", "D2", LocalDate.of(2025, 7, 1), TaskStatus.DONE);
        Task t3 = new Task(3, "T3", "D3", LocalDate.of(2025, 8, 1), TaskStatus.IN_PROGRESS);
        when(repository.findAll()).thenReturn(List.of(t1, t2, t3));

        List<Task> sorted = service.getAllTasksSortedByDueDate();

        assertEquals(List.of(t2, t3, t1), sorted);
    }

    @Test
    @DisplayName("Сортировка задач по статусу")
    void getAllTasksSortedByStatus_shouldReturnSorted() {
        Task t1 = new Task(1, "T1", "D1", LocalDate.now().plusDays(1), TaskStatus.DONE);
        Task t2 = new Task(2, "T2", "D2", LocalDate.now().plusDays(1), TaskStatus.TODO);
        Task t3 = new Task(3, "T3", "D3", LocalDate.now().plusDays(1), TaskStatus.IN_PROGRESS);
        when(repository.findAll()).thenReturn(List.of(t1, t2, t3));

        List<Task> sorted = service.getAllTasksSortedByStatus();

        assertEquals(List.of(t2, t3, t1), sorted);
    }
}
