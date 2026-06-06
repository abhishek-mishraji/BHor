package com.handsofretail.hor.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.handsofretail.hor.dto.request.StoreMemberRequest;
import com.handsofretail.hor.entity.ClientStoreId;
import com.handsofretail.hor.entity.ClientStoreMapping;
import com.handsofretail.hor.entity.ClientUser;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.enums.Status;
import com.handsofretail.hor.enums.StoreRole;
import com.handsofretail.hor.enums.UserRole;
import com.handsofretail.hor.repository.ClientStoreMappingRepository;
import com.handsofretail.hor.repository.ClientUserRepository;
import com.handsofretail.hor.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StoreMemberControllerIntegrationTest {

    private static final String BASE_URL = "/api/v1/admin/store-members";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private StoreRepository storeRepository;
    @Autowired private ClientUserRepository clientUserRepository;
    @Autowired private ClientStoreMappingRepository clientStoreMappingRepository;

    private Store savedStore;
    private ClientUser savedClient;

    @BeforeEach
    void setUp() {
        clientStoreMappingRepository.deleteAll();
        storeRepository.deleteAll();
        clientUserRepository.deleteAll();

        savedStore = storeRepository.save(Store.builder()
                .storeName("Test Store")
                .storeCode("TST-01")
                .status(Status.ACTIVE)
                .build());

        savedClient = clientUserRepository.save(ClientUser.builder()
                .fullName("Jane Doe")
                .email("jane@example.com")
                .passwordHash("hash")
                .status(Status.ACTIVE)
                .role(UserRole.CLIENT)
                .build());
    }

    // -------------------------------------------------------------------------
    // POST — success (PARTNER)
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void addStoreMember_shouldReturn201_whenPartnerIsAddedSuccessfully() throws Exception {
        StoreMemberRequest request = buildRequest(savedStore.getStoreId(), savedClient.getClientId(), StoreRole.PARTNER);

        mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Store member added successfully"))
                .andExpect(jsonPath("$.data.storeId").value(savedStore.getStoreId()))
                .andExpect(jsonPath("$.data.clientId").value(savedClient.getClientId()))
                .andExpect(jsonPath("$.data.clientName").value("Jane Doe"))
                .andExpect(jsonPath("$.data.role").value("PARTNER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addStoreMember_shouldReturn201_whenOwnerIsAddedToStoreWithNoExistingOwner() throws Exception {
        StoreMemberRequest request = buildRequest(savedStore.getStoreId(), savedClient.getClientId(), StoreRole.OWNER);

        mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("OWNER"));
    }

    // -------------------------------------------------------------------------
    // POST — 404
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void addStoreMember_shouldReturn404_whenStoreDoesNotExist() throws Exception {
        StoreMemberRequest request = buildRequest(9999L, savedClient.getClientId(), StoreRole.PARTNER);

        mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Store not found with id: 9999"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addStoreMember_shouldReturn404_whenClientDoesNotExist() throws Exception {
        StoreMemberRequest request = buildRequest(savedStore.getStoreId(), 9999L, StoreRole.PARTNER);

        mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Client not found with id: 9999"));
    }

    // -------------------------------------------------------------------------
    // POST — 409 duplicate
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void addStoreMember_shouldReturn409_whenMappingAlreadyExists() throws Exception {
        clientStoreMappingRepository.save(ClientStoreMapping.builder()
                .id(new ClientStoreId(savedClient.getClientId(), savedStore.getStoreId()))
                .client(savedClient)
                .store(savedStore)
                .role(StoreRole.PARTNER)
                .build());

        StoreMemberRequest request = buildRequest(savedStore.getStoreId(), savedClient.getClientId(), StoreRole.PARTNER);

        mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Client is already assigned to this store"));
    }

    // -------------------------------------------------------------------------
    // POST — 400 owner conflict
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void addStoreMember_shouldReturn400_whenStoreAlreadyHasOwner() throws Exception {
        // Seed an existing OWNER
        clientStoreMappingRepository.save(ClientStoreMapping.builder()
                .id(new ClientStoreId(savedClient.getClientId(), savedStore.getStoreId()))
                .client(savedClient)
                .store(savedStore)
                .role(StoreRole.OWNER)
                .build());

        // Try to add a second OWNER (a different client)
        ClientUser secondClient = clientUserRepository.save(ClientUser.builder()
                .fullName("Bob Smith")
                .email("bob@example.com")
                .passwordHash("hash")
                .status(Status.ACTIVE)
                .role(UserRole.CLIENT)
                .build());

        StoreMemberRequest request = buildRequest(savedStore.getStoreId(), secondClient.getClientId(), StoreRole.OWNER);

        mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Store already has an owner"));
    }

    // -------------------------------------------------------------------------
    // POST — 400 validation (missing fields)
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void addStoreMember_shouldReturn400_whenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isMap());
    }

    // -------------------------------------------------------------------------
    // GET — success
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStoreMembers_shouldReturn200_withMemberList() throws Exception {
        clientStoreMappingRepository.save(ClientStoreMapping.builder()
                .id(new ClientStoreId(savedClient.getClientId(), savedStore.getStoreId()))
                .client(savedClient)
                .store(savedStore)
                .role(StoreRole.OWNER)
                .build());

        mockMvc.perform(get(BASE_URL)
                        .param("storeId", String.valueOf(savedStore.getStoreId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Store members fetched"))
                .andExpect(jsonPath("$.data[0].clientName").value("Jane Doe"))
                .andExpect(jsonPath("$.data[0].role").value("OWNER"))
                .andExpect(jsonPath("$.data[0].storeId").value(savedStore.getStoreId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStoreMembers_shouldReturn404_whenStoreDoesNotExist() throws Exception {
        mockMvc.perform(get(BASE_URL).param("storeId", "9999"))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // DELETE — success
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeStoreMember_shouldReturn200_whenPartnerIsRemoved() throws Exception {
        clientStoreMappingRepository.save(ClientStoreMapping.builder()
                .id(new ClientStoreId(savedClient.getClientId(), savedStore.getStoreId()))
                .client(savedClient)
                .store(savedStore)
                .role(StoreRole.PARTNER)
                .build());

        mockMvc.perform(delete(BASE_URL + "/{storeId}/{clientId}",
                        savedStore.getStoreId(), savedClient.getClientId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Store member removed successfully"));
    }

    // -------------------------------------------------------------------------
    // DELETE — 400 cannot remove OWNER
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeStoreMember_shouldReturn400_whenAttemptingToRemoveOwner() throws Exception {
        clientStoreMappingRepository.save(ClientStoreMapping.builder()
                .id(new ClientStoreId(savedClient.getClientId(), savedStore.getStoreId()))
                .client(savedClient)
                .store(savedStore)
                .role(StoreRole.OWNER)
                .build());

        mockMvc.perform(delete(BASE_URL + "/{storeId}/{clientId}",
                        savedStore.getStoreId(), savedClient.getClientId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // -------------------------------------------------------------------------
    // DELETE — 404
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeStoreMember_shouldReturn404_whenMembershipDoesNotExist() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{storeId}/{clientId}",
                        savedStore.getStoreId(), savedClient.getClientId()))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // Security — unauthenticated
    // -------------------------------------------------------------------------

    @Test
    void addStoreMember_shouldReturn401_whenNoAuthToken() throws Exception {
        StoreMemberRequest request = buildRequest(1L, 1L, StoreRole.PARTNER);

        mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private StoreMemberRequest buildRequest(Long storeId, Long clientId, StoreRole role) {
        StoreMemberRequest req = new StoreMemberRequest();
        req.setStoreId(storeId);
        req.setClientId(clientId);
        req.setRole(role);
        return req;
    }
}
