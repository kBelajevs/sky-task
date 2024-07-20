package com.sky.kbelajevs.task.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    List<ProjectEntity> findByUsersEmail(String mail);

    Optional<ProjectEntity> findByName(String name);

    void deleteByName(String name);
}
