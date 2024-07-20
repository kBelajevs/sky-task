package com.sky.kbelajevs.task.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.kbelajevs.task.security.AuthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerIntegrationTest {

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    protected MockMvc mockMvc;

    protected String adminAuth;
    protected String userAuth;

    @BeforeEach
    public void setAuth(){
        setAdminAuth();
        setUserAuth();
    }

    protected void setAdminAuth() {
        try {
            var request = new AuthController.LoginRequest("admin", "test");
            var result = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            var responseDTO = MAPPER.readValue(responseBody, AuthController.LoginResponse.class);
            adminAuth = responseDTO.token();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to auth user");
        }
    }

    protected void setUserAuth() {
        try {
            var request = new AuthController.LoginRequest("user", "test");
            var result = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(MAPPER.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            var responseDTO = MAPPER.readValue(responseBody, AuthController.LoginResponse.class);
            userAuth = responseDTO.token();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to auth user");
        }
    }

    @Test
    void testGetUser_success() throws Exception {
        UserRequestDTO newUser = new UserRequestDTO("newuser@example.com", "New User", "password");
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminAuth)
                        .content(MAPPER.writeValueAsString(newUser)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{email}", "newuser@example.com").header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("newuser@example.com")) // Adjust based on your test data
                .andExpect(jsonPath("$.name").value("New User")); // Adjust based on your test data
    }

    @Test
    void testGetUser_NotFound() throws Exception {
        mockMvc.perform(get("/api/users/{email}", "notExisting").header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser_success() throws Exception {
        UserRequestDTO newUser = new UserRequestDTO("testCreateUser", "New User", "password");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(newUser))
                        .header("Authorization", "Bearer " + adminAuth)
                )
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{email}", "testCreateUser").header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("testCreateUser"));

    }

    @Test
    void testCreateUser_forbidden() throws Exception {
        UserRequestDTO newUser = new UserRequestDTO("testCreateUser", "New User", "password");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(newUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteUser_success() throws Exception {
        UserRequestDTO newUser = new UserRequestDTO("testDeleteUser", "New User", "password");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(newUser))
                        .header("Authorization", "Bearer " + adminAuth)
                )
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{email}", "testDeleteUser").header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("testDeleteUser"));

        mockMvc.perform(delete("/api/users/{email}", "testDeleteUser").header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{email}", "testDeleteUser").header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_forbidden() throws Exception {
        mockMvc.perform(delete("/api/users/{email}", "testDeleteUser"))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/users/{email}", "testDeleteUser").header("Authorization", "Bearer " + userAuth))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetUsers_success() throws Exception {
        mockMvc.perform(get("/api/users?page=0&size=10").header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items").isArray());
    }
}
