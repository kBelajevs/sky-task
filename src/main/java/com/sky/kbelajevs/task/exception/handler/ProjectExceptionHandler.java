package com.sky.kbelajevs.task.exception.handler;

import com.sky.kbelajevs.task.exception.ProjectNotFoundException;
import com.sky.kbelajevs.task.exception.RestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionHandler {

    @ExceptionHandler(value = ProjectNotFoundException.class)
    public ResponseEntity<RestException> exception(ProjectNotFoundException exception) {
        return new ResponseEntity<>(new RestException(exception), HttpStatus.NOT_FOUND);
    }
}
