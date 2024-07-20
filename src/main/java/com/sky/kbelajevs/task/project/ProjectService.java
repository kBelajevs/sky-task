package com.sky.kbelajevs.task.project;


import com.sky.kbelajevs.task.exception.ProjectNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public Page<ProjectEntity> findPage(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    public void create(ProjectEntity projectEntity) {
        projectRepository.save(projectEntity);
    }

    public ProjectEntity getProjectByName(String name) {
        return projectRepository.findByName(name).orElseThrow(ProjectNotFoundException::new);
    }

    @Transactional
    public void deleteProjectByName(String projectName) {
        projectRepository.deleteByName(projectName);
    }

    @Transactional
    public List<ProjectEntity> findProjectsByUserMail(String mail) {
        return projectRepository.findByUsersEmail(mail);
    }

    public ProjectEntity findByName(String name) {
        return projectRepository.findByName(name).orElseThrow(ProjectNotFoundException::new);
    }
}
