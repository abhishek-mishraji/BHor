package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FuelTypeResponseDTO {

    private Long fuelTypeId;

    private String fuelName;

    private Boolean active;
}