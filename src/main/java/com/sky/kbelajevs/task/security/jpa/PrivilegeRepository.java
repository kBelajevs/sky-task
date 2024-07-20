package com.sky.kbelajevs.task.security.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {

    PrivilegeEntity findByName(String name);
}
