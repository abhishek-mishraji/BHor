package com.handsofretail.hor.service;

import com.handsofretail.hor.dto.request.StoreMemberRequest;
import com.handsofretail.hor.dto.request.StoreRequest;
import com.handsofretail.hor.dto.request.StoreUpdateRequest;
import com.handsofretail.hor.dto.response.StoreMemberResponse;
import com.handsofretail.hor.dto.response.StoreResponse;
import com.handsofretail.hor.enums.Status;

import java.util.List;

public interface StoreService {

    StoreResponse createStore(StoreRequest request);

    List<StoreResponse> getMyStores(Long clientId);

    List<StoreResponse> getStoresByClientId(Long clientId);

    List<StoreResponse> getStores(Long clientId, Status status);

    StoreResponse getStoreById(Long storeId);

    StoreResponse updateStoreStatus(Long storeId, Status status);

    StoreResponse updateStore(Long storeId, StoreUpdateRequest request);

    List<StoreMemberResponse> getStoreMembers(Long storeId);

    StoreMemberResponse addStoreMember(StoreMemberRequest request);

    void removeStoreMember(Long storeId, Long clientId);
}
