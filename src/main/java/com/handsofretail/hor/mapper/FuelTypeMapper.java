package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.FuelTypeResponseDTO;
import com.handsofretail.hor.entity.FuelType;

public final class FuelTypeMapper {

    private FuelTypeMapper() {
    }

    public static FuelTypeResponseDTO toResponse(FuelType fuelType) {
        return FuelTypeResponseDTO.builder()
                .fuelTypeId(fuelType.getFuelTypeId())
                .fuelName(fuelType.getFuelName())
                .active(fuelType.getActive())
                .build();
    }
}