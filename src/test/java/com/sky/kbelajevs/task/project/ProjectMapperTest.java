package com.sky.kbelajevs.task.project;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectMapperTest {

    private static final ProjectMapper MAPPER =  new ProjectMapper();

    @Test
    public void testToEntity() {
        var requestDTO = new ProjectRequestDTO();
        requestDTO.setName("Test Project");

        var entity = MAPPER.toEntity(requestDTO);

        assertNotNull(entity);
        assertEquals("Test Project", entity.getName());
    }

    @Test
    public void testToEntity_Null() {
        var entity = MAPPER.toEntity(null);

        assertNull(entity);
    }

    @Test
    public void testToDTO() {
        var entity = new ProjectEntity();
        entity.setName("Test Project");

        var responseDTO = MAPPER.toDTO(entity);

        assertNotNull(responseDTO);
        assertEquals("Test Project", responseDTO.getName());
    }

    @Test
    public void testToDTO_Null() {
        var responseDTO = MAPPER.toDTO(null);

        assertNull(responseDTO);
    }
}
