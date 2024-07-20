package com.sky.kbelajevs.task.exception.handler;

import com.sky.kbelajevs.task.exception.RestException;
import com.sky.kbelajevs.task.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<RestException> exception(UserNotFoundException exception) {
        return new ResponseEntity<>(new RestException(exception), HttpStatus.NOT_FOUND);
    }
}
