package com.sky.kbelajevs.task.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO toDTO(UserEntity user) {
        if (user == null) {
            return null;
        }

        var dto = new UserResponseDTO();
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        return dto;
    }

    public UserEntity toEntity(UserRequestDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        var user = new UserEntity();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return user;
    }

}
