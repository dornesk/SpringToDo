package com.example.SpringToDo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //обрабатываем ошибки с некорректными аргументами (например, validateTask выбрасывает IllegalArgumentException)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //общий обработчик других исключений — возвращаем 500 и сообщение
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        return new ResponseEntity<>("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //обработка ошибки, когда элемент по ID не найден
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFound(TaskNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
