package com.repairmatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairmatch.modules.auth.dto.LoginRequest;
import com.repairmatch.modules.auth.dto.RegisterRequest;
import com.repairmatch.modules.technician.repository.TechnicianProfileRepository;
import com.repairmatch.modules.user.domain.User;
import com.repairmatch.modules.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TechnicianProfileRepository technicianProfileRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    void testRegisterNewCustomerSuccessfully() throws Exception {
        RegisterRequest req = new RegisterRequest(
                "neha.test@gmail.com",
                "password123",
                "Neha Kapoor",
                "+91 9988776655",
                "CUSTOMER"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.email").value("neha.test@gmail.com"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String token = jsonNode.get("token").asText();

        // Verify that /api/auth/me returns this customer's details using the token
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("neha.test@gmail.com"))
                .andExpect(jsonPath("$.fullName").value("Neha Kapoor"));
    }

    @Test
    void testRegisterTechnicianCreatesProfile() throws Exception {
        RegisterRequest req = new RegisterRequest(
                "arvind.tech@repairmatch.com",
                "password123",
                "Arvind Sharma",
                "+91 9988774433",
                "TECHNICIAN"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("TECHNICIAN"));

        User user = userRepository.findByEmail("arvind.tech@repairmatch.com").orElseThrow();
        assertNotNull(user);
        assertTrue(technicianProfileRepository.findByUserId(user.getId()).isPresent(),
                "Technician profile should be automatically provisioned");
    }

    @Test
    void testLoginWithValidDemoCredentials() throws Exception {
        LoginRequest req = new LoginRequest("admin@repairmatch.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.email").value("admin@repairmatch.com"));
    }

    @Test
    void testLoginWithInvalidPasswordFails() throws Exception {
        LoginRequest req = new LoginRequest("admin@repairmatch.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void testUnauthenticatedAccessToMeReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
