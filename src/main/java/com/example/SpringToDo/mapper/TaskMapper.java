package com.example.SpringToDo.mapper;

import com.example.SpringToDo.dto.TaskCreateDTO;
import com.example.SpringToDo.dto.TaskDTO;
import com.example.SpringToDo.model.Task;
import org.springframework.stereotype.Component;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

//@Mapper(componentModel = "spring")
//public interface TaskMapper {
//    TaskDTO toDto(Task task);
//    Task toEntity(TaskDTO dto);
//    Task toEntity(TaskCreateDTO dto);
//    List<TaskDTO> toDtoList(List<Task> tasks);
//}


@Component
public class TaskMapper {

    public TaskDTO toDto(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus()
        );
    }

    public Task toEntity(TaskDTO dto) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setStatus(dto.getStatus());
        return task;
    }

    public Task toEntity(TaskCreateDTO dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setStatus(dto.getStatus());
        return task;
    }

    public List<TaskDTO> toDtoList(List<Task> tasks) {
        return tasks.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
