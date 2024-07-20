package com.sky.kbelajevs.task.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserMapperTest {

    private static final PasswordEncoder ENCODER = mock(PasswordEncoder.class);
    private static final UserMapper MAPPER = new UserMapper(ENCODER);

    @Test
    void testToDTO() {
        var userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setName("Test User");

        var userDTO = MAPPER.toDTO(userEntity);

        assertNotNull(userDTO);
        assertEquals("test@example.com", userDTO.getEmail());
        assertEquals("Test User", userDTO.getName());
    }

    @Test
    void testToDTO_Null() {
        var userDTO = MAPPER.toDTO(null);

        assertNull(userDTO);
    }

    @Test
    void testToEntity() {
        when(ENCODER.encode(any())).thenReturn("encoded");
        var userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("test@example.com");
        userRequestDTO.setName("Test User");
        userRequestDTO.setPassword("password");

        var userEntity = MAPPER.toEntity(userRequestDTO);

        assertNotNull(userEntity);
        assertEquals("test@example.com", userEntity.getEmail());
        assertEquals("Test User", userEntity.getName());
        assertEquals("encoded", userEntity.getPassword());
    }

    @Test
    void testToEntity_Null() {
        var userEntity = MAPPER.toEntity(null);

        assertNull(userEntity);
    }
}
