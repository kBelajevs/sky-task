package com.sky.kbelajevs.task.exception;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException() {
        super("Project not found");
    }
}