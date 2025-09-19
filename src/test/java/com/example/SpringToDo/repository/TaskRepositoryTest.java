package com.example.SpringToDo.repository;

import com.example.SpringToDo.model.Task;
import com.example.SpringToDo.model.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Сохранение и поиск задачи в репозитории")
    void saveAndFindTask() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test description");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setStatus(TaskStatus.TODO);

        Task savedTask = taskRepository.save(task);

        assertThat(savedTask.getId()).isGreaterThan(0);

        Optional<Task> retrieved = taskRepository.findById(savedTask.getId());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTitle()).isEqualTo("Test Task");
        assertThat(retrieved.get().getStatus()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    @DisplayName("Удаление задачи из репозитория")
    void deleteTask() {
        Task task = new Task();
        task.setTitle("Delete Task");
        task.setDescription("Delete description");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setStatus(TaskStatus.DONE);

        Task savedTask = taskRepository.save(task);
        int id = savedTask.getId();

        taskRepository.deleteById(id);

        Optional<Task> deleted = taskRepository.findById(id);
        assertThat(deleted).isNotPresent();
    }

    @Test
    @DisplayName("Обновление задачи в репозитории")
    void updateTask() {
        Task task = new Task();
        task.setTitle("Initial Title");
        task.setDescription("Initial Description");
        task.setDueDate(LocalDate.now().plusDays(2));
        task.setStatus(TaskStatus.TODO);

        Task savedTask = taskRepository.save(task);

        savedTask.setTitle("Updated Title");
        savedTask.setStatus(TaskStatus.IN_PROGRESS);

        Task updatedTask = taskRepository.save(savedTask);

        Optional<Task> retrieved = taskRepository.findById(updatedTask.getId());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTitle()).isEqualTo("Updated Title");
        assertThat(retrieved.get().getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Поиск задач по статусу")
    void findTasksByStatus() {
        Task task1 = new Task();
        task1.setTitle("Task1");
        task1.setDescription("Desc1");
        task1.setDueDate(LocalDate.now().plusDays(1));
        task1.setStatus(TaskStatus.TODO);

        Task task2 = new Task();
        task2.setTitle("Task2");
        task2.setDescription("Desc2");
        task2.setDueDate(LocalDate.now().plusDays(2));
        task2.setStatus(TaskStatus.DONE);

        taskRepository.save(task1);
        taskRepository.save(task2);

        List<Task> todoTasks = taskRepository.findAll().stream()
                .filter(t -> t.getStatus() == TaskStatus.TODO)
                .toList();

        assertThat(todoTasks).hasSize(1);
        assertThat(todoTasks.get(0).getTitle()).isEqualTo("Task1");
    }
}
