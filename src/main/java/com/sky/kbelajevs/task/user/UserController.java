package com.sky.kbelajevs.task.user;

import com.sky.kbelajevs.task.common.PageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{email}")
    public UserResponseDTO getUser(@PathVariable("email") String email) {
        var userEntity =  userService.getUserByEmail(email);
        return userMapper.toDTO(userEntity);
    }

    @GetMapping(params = {"page", "size"})
    public PageDTO<UserResponseDTO> getUsers(@RequestParam("page") int page,
                                             @RequestParam("size") int size
    ) {
        var entityPage = userService.findPage(Pageable.ofSize(size).withPage(page));
        var pageDTO = new PageDTO<UserResponseDTO>(entityPage);
        var DTOs = entityPage.getContent().stream().map(userMapper::toDTO).toList();
        pageDTO.setItems(DTOs);
        return pageDTO;
    }

    @PostMapping
    public void createUser(@RequestBody UserRequestDTO userRequest) {
        userService.create(userMapper.toEntity(userRequest));
    }

    @DeleteMapping("/{email}")
    public void deleteUser(@PathVariable("email") String email) {
        userService.deleteUser(email);
    }

    @PostMapping("/addProject")
    public void addProjectToUser(@RequestBody AddProjectRequest request) {
        userService.assignExternalProjectToUser(request.userEmail(), request.projectName());
    }

    @DeleteMapping("/removeProject")
    public void deleteProjectFromUser(@RequestBody RemoveProjectRequest request) {
        userService.removerExternalProjectFromUser(request.userEmail(), request.projectName());
    }

    public record AddProjectRequest(String userEmail, String projectName) {
    }

    public record RemoveProjectRequest(String userEmail, String projectName) {
    }
}
