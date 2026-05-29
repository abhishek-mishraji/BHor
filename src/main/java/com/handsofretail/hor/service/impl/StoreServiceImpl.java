package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.dto.request.StoreRequest;
import com.handsofretail.hor.dto.request.StoreUpdateRequest;
import com.handsofretail.hor.dto.response.StoreResponse;
import com.handsofretail.hor.entity.ClientUser;

import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.enums.Status;
import com.handsofretail.hor.exception.DuplicateResourceException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.mapper.StoreMapper;
import com.handsofretail.hor.repository.ClientUserRepository;
import com.handsofretail.hor.repository.StoreRepository;
import com.handsofretail.hor.service.StoreService;
import com.handsofretail.hor.specification.StoreSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

        private final StoreRepository storeRepository;

        private final ClientUserRepository clientUserRepository;

        @Override
        public StoreResponse createStore(StoreRequest request) {

                boolean storeCodeExists = storeRepository
                                .existsByStoreCode(request.getStoreCode());

                if (storeCodeExists) {
                        throw new DuplicateResourceException("Store code already exists");
                }

                ClientUser clientUser = clientUserRepository
                                .findById(request.getClientId())
                                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

                Store store = Store.builder()
                                .client(clientUser)
                                .storeName(request.getStoreName())
                                .storeCode(request.getStoreCode())
                                .address(request.getAddress())
                                .contactNumber(request.getContactNumber())
                                .status(Status.ACTIVE)
                                .build();

                Store savedStore = storeRepository.save(store);

                return StoreMapper.toResponse(savedStore);
        }

        @Override
        public List<StoreResponse> getStoresByClientId(Long clientId) {

                return storeRepository.findByClientClientId(clientId)
                                .stream()
                                .map(StoreMapper::toResponse)
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
                                .map(StoreMapper::toResponse)
                                .toList();
        }

        @Override
        public StoreResponse getStoreById(Long storeId) {

                Store store = storeRepository.findById(storeId)
                                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

                return StoreMapper.toResponse(store);
        }

        @Override
        public StoreResponse updateStoreStatus(Long storeId, Status status) {

                Store store = storeRepository.findById(storeId)
                                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

                store.setStatus(status);

                Store savedStore = storeRepository.save(store);

                return StoreMapper.toResponse(savedStore);
        }

        @Override
        public StoreResponse updateStore(Long storeId, StoreUpdateRequest request) {

                Store store = storeRepository.findById(storeId)
                                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

                if (request.getClientId() != null
                                && !request.getClientId().equals(store.getClient().getClientId())) {

                        ClientUser clientUser = clientUserRepository.findById(request.getClientId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
                        store.setClient(clientUser);
                }

                if (request.getStoreCode() != null
                                && !request.getStoreCode().equals(store.getStoreCode())) {

                        boolean storeCodeExists = storeRepository.existsByStoreCode(request.getStoreCode());
                        if (storeCodeExists) {
                                throw new DuplicateResourceException("Store code already exists");
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

                Store updatedStore = storeRepository.save(store);
                return StoreMapper.toResponse(updatedStore);
        }
}