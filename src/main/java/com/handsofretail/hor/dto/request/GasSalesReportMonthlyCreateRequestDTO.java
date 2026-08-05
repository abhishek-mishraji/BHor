package com.handsofretail.hor.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class GasSalesReportMonthlyCreateRequestDTO {

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
    private BigDecimal creditFees;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @JsonAlias("total_volume_sold")
    private BigDecimal totalVolumeSold;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @JsonAlias("net_profit_per_gallon")
    private BigDecimal netProfitPerGallon;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    @JsonAlias("net_profit")
    private BigDecimal netProfit;

    @NotNull
    @Valid
    private List<GasSalesReportDetailRequestDTO> details;
}
