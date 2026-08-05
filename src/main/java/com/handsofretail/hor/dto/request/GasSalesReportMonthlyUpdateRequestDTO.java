package com.handsofretail.hor.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class GasSalesReportMonthlyUpdateRequestDTO {

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal creditFees;

    @NotNull
    @Valid
    private List<GasSalesReportDetailRequestDTO> details;
}