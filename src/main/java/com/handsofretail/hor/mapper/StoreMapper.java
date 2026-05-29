package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.StoreResponse;
import com.handsofretail.hor.entity.Store;

public final class StoreMapper {

    private StoreMapper() {
    }

    public static StoreResponse toResponse(Store store) {
        return StoreResponse.builder()
                .storeId(store.getStoreId())
                .clientId(store.getClient().getClientId())
                .clientName(store.getClient().getFullName())
                .storeName(store.getStoreName())
                .storeCode(store.getStoreCode())
                .address(store.getAddress())
                .contactNumber(store.getContactNumber())
                .status(store.getStatus().name())
                .build();
    }
}