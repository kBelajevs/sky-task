package com.sky.kbelajevs.task.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.kbelajevs.task.security.AuthController;
import com.sky.kbelajevs.task.user.UserController;
import com.sky.kbelajevs.task.user.UserRequestDTO;

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
public class ProjectControllerIntegrationTest {

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
    void testGetProject_success() throws Exception {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("testGetProject_success");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminAuth)
                        .content(MAPPER.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/projects/{projectName}", "testGetProject_success").header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("testGetProject_success"));
    }

    @Test
    void testGetProject_NotFound() throws Exception {
        mockMvc.perform(get("/api/projects/{projectName}", "project999").header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Project not found"));
    }

    @Test
    void testGetProject_Forbidden() throws Exception {
        mockMvc.perform(get("/api/projects/{projectName}", "project999"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetProjects_success() throws Exception {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("testGetProjects_success");
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminAuth)
                        .content(MAPPER.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());


        mockMvc.perform(get("/api/projects")
                        .param("page", "0")
                        .header("Authorization", "Bearer " + adminAuth)
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0]").exists());
    }

    @Test
    void testGetProjects_Forbidden() throws Exception {
        mockMvc.perform(get("/api/projects")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateProject_success() throws Exception {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("testCreateProject_success");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(requestDTO))
                        .header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateProject_Forbidden_for_user() throws Exception {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("testCreateProject_success");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(requestDTO))
                        .header("Authorization", "Bearer " + userAuth))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateProject_Forbidden() throws Exception {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("testCreateProject_success");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteProject_success() throws Exception {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("testDeleteProject_success");
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminAuth)
                        .content(MAPPER.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/projects/{projectId}", "testDeleteProject_success")  .header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/projects/{projectName}", "testDeleteProject_success").header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Project not found"));
    }

    @Test
    void testGetProjectsOfAUser_success() throws Exception {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("project_to_add");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(requestDTO))
                        .header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isOk());

        UserRequestDTO newUser = new UserRequestDTO("user_with_project", "New User", "password");
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(newUser))
                        .header("Authorization", "Bearer " + adminAuth)
                )
                .andExpect(status().isOk());


        var addProjectToUserRequest = new UserController.AddProjectRequest("user_with_project", "project_to_add");
        mockMvc.perform(post("/api/users/addProject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(addProjectToUserRequest))
                        .header("Authorization", "Bearer " + adminAuth)
                )
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/projects/userProjects/{email}", "user_with_project").header("Authorization", "Bearer " + adminAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value("project_to_add"));

    }
}
