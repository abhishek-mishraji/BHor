package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.StoreFuelTypeResponseDTO;
import com.handsofretail.hor.entity.StoreFuelType;

public final class StoreFuelTypeMapper {

    private StoreFuelTypeMapper() {
    }

    public static StoreFuelTypeResponseDTO toResponse(StoreFuelType storeFuelType) {
        return StoreFuelTypeResponseDTO.builder()
                .storeFuelTypeId(storeFuelType.getStoreFuelTypeId())
                .storeId(storeFuelType.getStore().getStoreId())
                .fuelTypeId(storeFuelType.getFuelType().getFuelTypeId())
                .fuelName(storeFuelType.getFuelType().getFuelName())
                .active(storeFuelType.getActive())
                .build();
    }
}