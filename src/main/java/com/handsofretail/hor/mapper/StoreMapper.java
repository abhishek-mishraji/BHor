package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.StoreResponse;
import com.handsofretail.hor.entity.ClientUser;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.enums.StoreRole;

public final class StoreMapper {

    private StoreMapper() {
    }

    /** Admin context — clientRole omitted from response. */
    public static StoreResponse toResponse(Store store, ClientUser owner) {
        return StoreResponse.builder()
                .storeId(store.getStoreId())
                .clientId(owner != null ? owner.getClientId() : null)
                .clientName(owner != null ? owner.getFullName() : null)
                .storeName(store.getStoreName())
                .storeCode(store.getStoreCode())
                .address(store.getAddress())
                .contactNumber(store.getContactNumber())
                .status(store.getStatus().name())
                .build();
    }

    /** Client context — includes the caller's role in this store. */
    public static StoreResponse toResponse(Store store, ClientUser owner, StoreRole clientRole) {
        StoreResponse response = toResponse(store, owner);
        response.setClientRole(clientRole != null ? clientRole.name() : null);
        return response;
    }
}
