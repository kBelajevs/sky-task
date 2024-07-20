package com.sky.kbelajevs.task.exception;

import lombok.Getter;

@Getter
public class RestException {

    private final String message;

    public RestException(RuntimeException exception) {
        this.message = exception.getMessage();
    }

}
