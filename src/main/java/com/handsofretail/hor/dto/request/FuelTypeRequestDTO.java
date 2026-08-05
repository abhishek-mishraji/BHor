package com.handsofretail.hor.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuelTypeRequestDTO {

    @NotBlank
    private String fuelName;

    private Boolean active;
}