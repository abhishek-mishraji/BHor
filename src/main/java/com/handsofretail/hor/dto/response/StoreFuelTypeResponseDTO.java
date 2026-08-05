package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StoreFuelTypeResponseDTO {

    private Long storeFuelTypeId;

    private Long storeId;

    private Long fuelTypeId;

    private String fuelName;

    private Boolean active;
}