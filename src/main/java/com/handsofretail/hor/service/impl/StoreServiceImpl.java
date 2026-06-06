package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.dto.request.StoreMemberRequest;
import com.handsofretail.hor.dto.request.StoreRequest;
import com.handsofretail.hor.dto.request.StoreUpdateRequest;
import com.handsofretail.hor.dto.response.StoreMemberResponse;
import com.handsofretail.hor.dto.response.StoreResponse;
import com.handsofretail.hor.entity.ClientStoreId;
import com.handsofretail.hor.entity.ClientStoreMapping;
import com.handsofretail.hor.entity.ClientUser;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.enums.Status;
import com.handsofretail.hor.enums.StoreRole;
import com.handsofretail.hor.exception.BadRequestException;
import com.handsofretail.hor.exception.DuplicateResourceException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.mapper.StoreMapper;
import com.handsofretail.hor.repository.ClientStoreMappingRepository;
import com.handsofretail.hor.repository.ClientUserRepository;
import com.handsofretail.hor.repository.StoreRepository;
import com.handsofretail.hor.service.StoreService;
import com.handsofretail.hor.specification.StoreSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final ClientUserRepository clientUserRepository;
    private final ClientStoreMappingRepository clientStoreMappingRepository;

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public StoreResponse createStore(StoreRequest request) {

        if (storeRepository.existsByStoreCode(request.getStoreCode())) {
            throw new DuplicateResourceException(
                    "Store code already in use: " + request.getStoreCode());
        }

        ClientUser owner = clientUserRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found with id: " + request.getClientId()));

        Store store = Store.builder()
                .storeName(request.getStoreName())
                .storeCode(request.getStoreCode())
                .address(request.getAddress())
                .contactNumber(request.getContactNumber())
                .status(Status.ACTIVE)
                .build();

        Store savedStore = storeRepository.save(store);

        ClientStoreMapping ownerMapping = ClientStoreMapping.builder()
                .id(new ClientStoreId(owner.getClientId(), savedStore.getStoreId()))
                .client(owner)
                .store(savedStore)
                .role(StoreRole.OWNER)
                .build();

        clientStoreMappingRepository.save(ownerMapping);

        return StoreMapper.toResponse(savedStore, owner);
    }

    // -------------------------------------------------------------------------
    // Read
    // -------------------------------------------------------------------------

    @Override
    public List<StoreResponse> getMyStores(Long clientId) {
        List<ClientStoreMapping> myMappings =
                clientStoreMappingRepository.findMappingsWithDetailsByClientId(clientId);

        return myMappings.stream()
                .map(m -> {
                    ClientUser owner = resolveOwner(m.getStore().getStoreId());
                    return StoreMapper.toResponse(m.getStore(), owner, m.getRole());
                })
                .toList();
    }

    @Override
    public List<StoreResponse> getStoresByClientId(Long clientId) {
        List<ClientStoreMapping> myMappings =
                clientStoreMappingRepository.findMappingsWithDetailsByClientId(clientId);

        return myMappings.stream()
                .map(m -> {
                    ClientUser owner = resolveOwner(m.getStore().getStoreId());
                    return StoreMapper.toResponse(m.getStore(), owner);
                })
                .toList();
    }

    @Override
    public List<StoreResponse> getStores(Long clientId, Status status) {
        Specification<Store> spec = (root, query, cb) -> null;

        if (clientId != null) {
            spec = spec.and(StoreSpecification.hasClientId(clientId));
        }

        if (status != null) {
            spec = spec.and(StoreSpecification.hasStatus(status));
        }

        return storeRepository.findAll(spec)
                .stream()
                .map(store -> StoreMapper.toResponse(store, resolveOwner(store.getStoreId())))
                .toList();
    }

    @Override
    public StoreResponse getStoreById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store not found with id: " + storeId));

        return StoreMapper.toResponse(store, resolveOwner(storeId));
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    @Override
    public StoreResponse updateStoreStatus(Long storeId, Status status) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store not found with id: " + storeId));

        store.setStatus(status);
        Store saved = storeRepository.save(store);
        return StoreMapper.toResponse(saved, resolveOwner(storeId));
    }

    @Override
    @Transactional
    public StoreResponse updateStore(Long storeId, StoreUpdateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store not found with id: " + storeId));

        // Reassign OWNER when clientId is supplied
        if (request.getClientId() != null) {
            ClientUser newOwner = clientUserRepository.findById(request.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Client not found with id: " + request.getClientId()));

            ClientStoreMapping oldOwnerMapping = clientStoreMappingRepository
                    .findByIdStoreIdAndRole(storeId, StoreRole.OWNER)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Store has no OWNER mapping — data integrity issue for store id: " + storeId));

            // Only act if the owner is actually changing
            if (!oldOwnerMapping.getId().getClientId().equals(newOwner.getClientId())) {
                clientStoreMappingRepository.delete(oldOwnerMapping);
                clientStoreMappingRepository.save(ClientStoreMapping.builder()
                        .id(new ClientStoreId(newOwner.getClientId(), storeId))
                        .client(newOwner)
                        .store(store)
                        .role(StoreRole.OWNER)
                        .build());
            }
        }

        if (request.getStoreCode() != null
                && !request.getStoreCode().equals(store.getStoreCode())) {
            if (storeRepository.existsByStoreCode(request.getStoreCode())) {
                throw new DuplicateResourceException(
                        "Store code already in use: " + request.getStoreCode());
            }
            store.setStoreCode(request.getStoreCode());
        }

        if (request.getStoreName() != null) {
            store.setStoreName(request.getStoreName());
        }

        if (request.getAddress() != null) {
            store.setAddress(request.getAddress());
        }

        if (request.getContactNumber() != null) {
            store.setContactNumber(request.getContactNumber());
        }

        Store updated = storeRepository.save(store);
        return StoreMapper.toResponse(updated, resolveOwner(storeId));
    }

    // -------------------------------------------------------------------------
    // Member management
    // -------------------------------------------------------------------------

    @Override
    public List<StoreMemberResponse> getStoreMembers(Long storeId) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store not found with id: " + storeId));

        return clientStoreMappingRepository.findAllMembersWithClientByStoreId(storeId)
                .stream()
                .map(m -> StoreMemberResponse.builder()
                        .clientId(m.getClient().getClientId())
                        .clientName(m.getClient().getFullName())
                        .role(m.getRole().name())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public StoreMemberResponse addStoreMember(Long storeId, StoreMemberRequest request) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store not found with id: " + storeId));

        ClientUser client = clientUserRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found with id: " + request.getClientId()));

        if (clientStoreMappingRepository.existsByIdClientIdAndIdStoreId(
                request.getClientId(), storeId)) {
            throw new DuplicateResourceException(
                    "Client " + request.getClientId() + " is already a member of store " + storeId);
        }

        if (request.getRole() == StoreRole.OWNER) {
            throw new BadRequestException(
                    "Cannot add a second OWNER. Use the update store endpoint to reassign ownership.");
        }

        Store store = storeRepository.getReferenceById(storeId);

        ClientStoreMapping mapping = ClientStoreMapping.builder()
                .id(new ClientStoreId(client.getClientId(), storeId))
                .client(client)
                .store(store)
                .role(request.getRole())
                .build();

        clientStoreMappingRepository.save(mapping);

        return StoreMemberResponse.builder()
                .clientId(client.getClientId())
                .clientName(client.getFullName())
                .role(mapping.getRole().name())
                .build();
    }

    @Override
    @Transactional
    public void removeStoreMember(Long storeId, Long clientId) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store not found with id: " + storeId));

        if (!clientStoreMappingRepository.existsByIdClientIdAndIdStoreId(clientId, storeId)) {
            throw new ResourceNotFoundException(
                    "Client " + clientId + " is not a member of store " + storeId);
        }

        ClientStoreMapping mapping = clientStoreMappingRepository
                .findByIdStoreIdAndRole(storeId, StoreRole.OWNER)
                .orElse(null);

        if (mapping != null && mapping.getId().getClientId().equals(clientId)) {
            throw new BadRequestException(
                    "Cannot remove the OWNER from a store. Reassign ownership first.");
        }

        clientStoreMappingRepository.deleteByIdClientIdAndIdStoreId(clientId, storeId);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private ClientUser resolveOwner(Long storeId) {
        return clientStoreMappingRepository
                .findOwnerMappingByStoreId(storeId, StoreRole.OWNER)
                .map(ClientStoreMapping::getClient)
                .orElse(null);
    }
}
