package com.handsofretail.hor.service;

import com.handsofretail.hor.dto.request.StoreMemberRequest;
import com.handsofretail.hor.dto.response.StoreMemberResponse;
import com.handsofretail.hor.entity.ClientStoreId;
import com.handsofretail.hor.entity.ClientStoreMapping;
import com.handsofretail.hor.entity.ClientUser;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.enums.StoreRole;
import com.handsofretail.hor.exception.BadRequestException;
import com.handsofretail.hor.exception.DuplicateResourceException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.repository.ClientStoreMappingRepository;
import com.handsofretail.hor.repository.ClientUserRepository;
import com.handsofretail.hor.repository.StoreRepository;
import com.handsofretail.hor.service.impl.StoreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreMemberServiceTest {

    @Mock private StoreRepository storeRepository;
    @Mock private ClientUserRepository clientUserRepository;
    @Mock private ClientStoreMappingRepository clientStoreMappingRepository;

    @InjectMocks
    private StoreServiceImpl storeService;

    private Store store;
    private ClientUser client;

    @BeforeEach
    void setUp() {
        store = Store.builder().storeName("Test Store").storeCode("TST-01").build();
        // Simulate a persisted ID via reflection-free builder — real ID set by DB; use a stub
        client = ClientUser.builder().fullName("Jane Doe").email("jane@example.com").build();
    }

    private StoreMemberRequest buildRequest(Long storeId, Long clientId, StoreRole role) {
        StoreMemberRequest req = new StoreMemberRequest();
        req.setStoreId(storeId);
        req.setClientId(clientId);
        req.setRole(role);
        return req;
    }

    // -------------------------------------------------------------------------
    // addStoreMember — happy paths
    // -------------------------------------------------------------------------

    @Test
    void addStoreMember_shouldAddPartner_whenAllConditionsMet() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(clientUserRepository.findById(3L)).thenReturn(Optional.of(client));
        when(clientStoreMappingRepository.existsByIdClientIdAndIdStoreId(3L, 1L)).thenReturn(false);
        when(clientStoreMappingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StoreMemberResponse response = storeService.addStoreMember(buildRequest(1L, 3L, StoreRole.PARTNER));

        assertThat(response.getRole()).isEqualTo("PARTNER");
        assertThat(response.getClientName()).isEqualTo("Jane Doe");
        assertThat(response.getStoreId()).isEqualTo(1L);
        verify(clientStoreMappingRepository).save(any(ClientStoreMapping.class));
    }

    @Test
    void addStoreMember_shouldAddOwner_whenStoreHasNoExistingOwner() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(clientUserRepository.findById(3L)).thenReturn(Optional.of(client));
        when(clientStoreMappingRepository.existsByIdClientIdAndIdStoreId(3L, 1L)).thenReturn(false);
        when(clientStoreMappingRepository.findByIdStoreIdAndRole(1L, StoreRole.OWNER))
                .thenReturn(Optional.empty());
        when(clientStoreMappingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StoreMemberResponse response = storeService.addStoreMember(buildRequest(1L, 3L, StoreRole.OWNER));

        assertThat(response.getRole()).isEqualTo("OWNER");
        verify(clientStoreMappingRepository).save(any(ClientStoreMapping.class));
    }

    // -------------------------------------------------------------------------
    // addStoreMember — 404 paths
    // -------------------------------------------------------------------------

    @Test
    void addStoreMember_shouldThrow404_whenStoreDoesNotExist() {
        when(storeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storeService.addStoreMember(buildRequest(99L, 3L, StoreRole.PARTNER)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Store not found");
    }

    @Test
    void addStoreMember_shouldThrow404_whenClientDoesNotExist() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(clientUserRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storeService.addStoreMember(buildRequest(1L, 99L, StoreRole.PARTNER)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Client not found");
    }

    // -------------------------------------------------------------------------
    // addStoreMember — 409 duplicate
    // -------------------------------------------------------------------------

    @Test
    void addStoreMember_shouldThrow409_whenMappingAlreadyExists() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(clientUserRepository.findById(3L)).thenReturn(Optional.of(client));
        when(clientStoreMappingRepository.existsByIdClientIdAndIdStoreId(3L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> storeService.addStoreMember(buildRequest(1L, 3L, StoreRole.PARTNER)))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Client is already assigned to this store");
    }

    // -------------------------------------------------------------------------
    // addStoreMember — 400 owner conflict
    // -------------------------------------------------------------------------

    @Test
    void addStoreMember_shouldThrow400_whenStoreAlreadyHasOwner() {
        ClientStoreMapping existingOwner = ClientStoreMapping.builder()
                .id(new ClientStoreId(2L, 1L))
                .client(client)
                .store(store)
                .role(StoreRole.OWNER)
                .build();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(clientUserRepository.findById(3L)).thenReturn(Optional.of(client));
        when(clientStoreMappingRepository.existsByIdClientIdAndIdStoreId(3L, 1L)).thenReturn(false);
        when(clientStoreMappingRepository.findByIdStoreIdAndRole(1L, StoreRole.OWNER))
                .thenReturn(Optional.of(existingOwner));

        assertThatThrownBy(() -> storeService.addStoreMember(buildRequest(1L, 3L, StoreRole.OWNER)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Store already has an owner");
    }

    // -------------------------------------------------------------------------
    // addStoreMember — no extra repo calls on early throw
    // -------------------------------------------------------------------------

    @Test
    void addStoreMember_shouldNotSave_whenStoreNotFound() {
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storeService.addStoreMember(buildRequest(1L, 3L, StoreRole.PARTNER)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(clientStoreMappingRepository, never()).save(any());
    }
}
