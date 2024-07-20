package com.sky.kbelajevs.task.user;

import com.sky.kbelajevs.task.exception.UserNotFoundException;
import com.sky.kbelajevs.task.project.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProjectService projectService;

    public Page<UserEntity> findPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void create(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void deleteUser(String email) {
        userRepository.deleteByEmail(email);
    }

    @Transactional
    public void assignExternalProjectToUser(String userMail, String projectName) {
        var project = projectService.findByName(projectName);
        var user = getUserByEmail(userMail);

        user.getProjects().add(project);

    }

    @Transactional
    public void removerExternalProjectFromUser(String userMail, String projectName) {
        var project = projectService.findByName(projectName);
        var user = getUserByEmail(userMail);

        user.getProjects().remove(project);
    }
}
