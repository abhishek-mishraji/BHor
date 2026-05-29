package com.handsofretail.hor.service;

import java.util.List;

import com.handsofretail.hor.dto.request.StoreRequest;
import com.handsofretail.hor.dto.request.StoreUpdateRequest;
import com.handsofretail.hor.dto.response.StoreResponse;
import com.handsofretail.hor.enums.Status;

public interface StoreService {

    StoreResponse createStore(StoreRequest request);

    List<StoreResponse> getStoresByClientId(Long clientId);

    List<StoreResponse> getStores(Long clientId, Status status);

    StoreResponse getStoreById(Long storeId);

    StoreResponse updateStoreStatus(Long storeId, Status status);

    StoreResponse updateStore(Long storeId, StoreUpdateRequest request);
}