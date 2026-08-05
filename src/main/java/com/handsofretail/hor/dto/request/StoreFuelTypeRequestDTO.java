package com.handsofretail.hor.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StoreFuelTypeRequestDTO {

    @NotNull
    private List<@NotNull Long> fuelTypeIds;
}