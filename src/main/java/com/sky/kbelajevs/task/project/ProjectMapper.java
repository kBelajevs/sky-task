package com.sky.kbelajevs.task.project;

import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectEntity toEntity(ProjectRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        var entity = new ProjectEntity();
        entity.setName(requestDTO.getName());
        return entity;
    }

    public ProjectResponseDTO toDTO(ProjectEntity entity) {
        if (entity == null) {
            return null;
        }
        var responseDTO = new ProjectResponseDTO();
        responseDTO.setName(entity.getName());
        return responseDTO;
    }
}
