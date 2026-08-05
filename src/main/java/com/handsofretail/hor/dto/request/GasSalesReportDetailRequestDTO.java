package com.handsofretail.hor.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GasSalesReportDetailRequestDTO {

    @NotNull
    private Long fuelTypeId;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal volumeSold;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal profitPerGallon;
}