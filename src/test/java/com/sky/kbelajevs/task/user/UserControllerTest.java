package com.sky.kbelajevs.task.user;


import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private final static UserService SERVICE = mock(UserService.class);
    private final static UserMapper MAPPER = mock(UserMapper.class);
    private final static UserController CONTROLLER = new UserController(SERVICE, MAPPER);

    @Before
    public void resetMocks() {
        reset(SERVICE, MAPPER);
    }

    @Test
    public void testGetUser() {
        var userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setName("Test User");

        when(SERVICE.getUserByEmail("test@example.com")).thenReturn(userEntity);
        when(MAPPER.toDTO(userEntity)).thenReturn(new UserResponseDTO("test@example.com", "Test User"));

        var userResponse = CONTROLLER.getUser("test@example.com");

        assertNotNull(userResponse);
        assertEquals("test@example.com", userResponse.getEmail());
        assertEquals("Test User", userResponse.getName());
        verify(SERVICE).getUserByEmail("test@example.com");
        verify(MAPPER).toDTO(userEntity);
    }

    @Test
    public void testCreateUser() {
        var userRequest = new UserRequestDTO("test@example.com", "Test User", "password");
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setName("Test User");

        when(MAPPER.toEntity(userRequest)).thenReturn(userEntity);

        CONTROLLER.createUser(userRequest);

        verify(SERVICE).create(userEntity);
    }

    @Test
    public void testDeleteUser() {
        CONTROLLER.deleteUser("email");

        verify(SERVICE).deleteUser("email");
    }
}
