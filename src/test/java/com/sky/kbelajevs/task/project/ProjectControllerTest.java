package com.sky.kbelajevs.task.project;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ProjectControllerTest {

    private static final ProjectService SERVICE = mock(ProjectService.class);
    private static final ProjectMapper MAPPER = mock(ProjectMapper.class);
    private static final ProjectController CONTROLLER = new ProjectController(SERVICE, MAPPER);

    @Before
    public void resetMocks() {
        reset(SERVICE, MAPPER);
    }

    @Test
    public void testGetProject() {
        var projectName = "1";
        var project = new ProjectEntity();
        var responseDTO = new ProjectResponseDTO();
        when(SERVICE.getProjectByName(projectName)).thenReturn(project);
        when(MAPPER.toDTO(project)).thenReturn(responseDTO);

        var result = CONTROLLER.getProject(projectName);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(SERVICE, times(1)).getProjectByName(projectName);
        verify(MAPPER, times(1)).toDTO(project);
    }

    @Test
    public void testGetProjects() {
        int page = 0;
        int size = 10;
        var project = new ProjectEntity();
        var responseDTO = new ProjectResponseDTO();
        var projectPage = new PageImpl<>(List.of(project), PageRequest.of(page, size), 1);
        when(SERVICE.findPage(any(Pageable.class))).thenReturn(projectPage);
        when(MAPPER.toDTO(project)).thenReturn(responseDTO);

        var result = CONTROLLER.getProjects(page, size);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(responseDTO, result.getItems().getFirst());
        verify(SERVICE, times(1)).findPage(any(Pageable.class));
        verify(MAPPER, times(1)).toDTO(project);
    }

    @Test
    public void testCreateProject() {
        var requestDTO = new ProjectRequestDTO();
        var project = new ProjectEntity();
        when(MAPPER.toEntity(requestDTO)).thenReturn(project);

        CONTROLLER.createProject(requestDTO);

        verify(MAPPER, times(1)).toEntity(requestDTO);
        verify(SERVICE, times(1)).create(project);
    }

    @Test
    public void testDeleteProject() {
        CONTROLLER.deleteProject("projectName");

        verify(SERVICE, times(1)).deleteProjectByName("projectName");
    }

    @Test
    public void testGetProjectsOfAUser() {
        var mail = "test";
        var project = new ProjectEntity();
        var responseDTO = new ProjectResponseDTO();
        when(SERVICE.findProjectsByUserMail(mail)).thenReturn(List.of(project));
        when(MAPPER.toDTO(project)).thenReturn(responseDTO);

        var result = CONTROLLER.getProjectsOfAUser(mail);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDTO, result.getFirst());
        verify(SERVICE, times(1)).findProjectsByUserMail(mail);
        verify(MAPPER, times(1)).toDTO(project);
    }
}

