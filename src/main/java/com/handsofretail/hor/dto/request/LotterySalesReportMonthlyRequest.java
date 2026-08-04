package com.handsofretail.hor.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LotterySalesReportMonthlyRequest {

    @NotNull
    private Long storeId;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer reportMonth;

    @NotNull
    @Min(1)
    @Max(9999)
    private Integer reportYear;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal onlineSales;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal scratchOffSales;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal onlineCashes;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal scratchOffCashes;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal commission;
}