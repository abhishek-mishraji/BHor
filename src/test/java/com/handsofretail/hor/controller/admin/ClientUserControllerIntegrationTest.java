package com.handsofretail.hor.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handsofretail.hor.dto.request.ClientUserRequest;
import com.handsofretail.hor.entity.AdminUser;
import com.handsofretail.hor.entity.ClientUser;
import com.handsofretail.hor.enums.Status;
import com.handsofretail.hor.enums.UserRole;
import com.handsofretail.hor.repository.AdminUserRepository;
import com.handsofretail.hor.repository.ClientUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClientUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private ClientUserRepository clientUserRepository;

    @BeforeEach
    void setUp() {
        clientUserRepository.deleteAll();
        adminUserRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createClientShouldReturnConflictWhenEmailExistsInAdminUsers() throws Exception {
        adminUserRepository.save(AdminUser.builder()
                .fullName("Admin User")
                .email("shared@company.com")
                .passwordHash("encoded-password")
                .role(UserRole.ADMIN)
                .build());

        ClientUserRequest request = buildRequest("shared@company.com");

        mockMvc.perform(post("/api/v1/admin/clients")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createClientShouldReturnConflictWhenEmailExistsInClientUsers() throws Exception {
        clientUserRepository.save(ClientUser.builder()
                .fullName("Existing Client")
                .email("shared@company.com")
                .passwordHash("encoded-password")
                .phoneNumber("1234567890")
                .address("Test Address")
                .status(Status.ACTIVE)
                .role(UserRole.CLIENT)
                .build());

        ClientUserRequest request = buildRequest("shared@company.com");

        mockMvc.perform(post("/api/v1/admin/clients")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createClientShouldCreateUserWhenEmailIsGloballyUnique() throws Exception {
        ClientUserRequest request = buildRequest("unique@company.com");

        mockMvc.perform(post("/api/v1/admin/clients")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Client created"))
                .andExpect(jsonPath("$.data.email").value("unique@company.com"))
                .andExpect(jsonPath("$.data.role").value("CLIENT"));
    }

    private ClientUserRequest buildRequest(String email) {
        ClientUserRequest request = new ClientUserRequest();
        request.setFullName("Test Client");
        request.setEmail(email);
        request.setPassword("Password@123");
        request.setPhoneNumber("9999999999");
        request.setAddress("Test Address");
        return request;
    }
}
