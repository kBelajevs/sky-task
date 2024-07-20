package com.sky.kbelajevs.task.project;


import com.sky.kbelajevs.task.common.PageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController  {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    @GetMapping("/{projectName}")
    public ProjectResponseDTO getProject(@PathVariable("projectName") String projectName) {
        var entity =  projectService.getProjectByName(projectName);
        return projectMapper.toDTO(entity);
    }

    @GetMapping(params = {"page", "size"})
    public PageDTO<ProjectResponseDTO> getProjects(@RequestParam("page") int page,
                                          @RequestParam("size") int size
    ) {
        var entityPage = projectService.findPage(Pageable.ofSize(size).withPage(page));
        var pageDTO = new PageDTO<ProjectResponseDTO>(entityPage);
        var DTOs = entityPage.getContent().stream().map(projectMapper::toDTO).toList();
        pageDTO.setItems(DTOs);
        return pageDTO;
    }

    @PostMapping
    public void createProject(@RequestBody ProjectRequestDTO projectRequest) {
        var entity = projectMapper.toEntity(projectRequest);
        projectService.create(entity);

    }

    @DeleteMapping("/{projectName}")
    public void deleteProject(@PathVariable("projectName") String projectName) {
        projectService.deleteProjectByName(projectName);
    }

    @GetMapping("/userProjects/{email}")
    public List<ProjectResponseDTO> getProjectsOfAUser(@PathVariable("email") String email) {
        var projects = projectService.findProjectsByUserMail(email);
        return projects.stream().map(projectMapper::toDTO).toList();
    }
}
