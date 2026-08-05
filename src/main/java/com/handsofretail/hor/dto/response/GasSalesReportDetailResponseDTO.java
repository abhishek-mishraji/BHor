package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class GasSalesReportDetailResponseDTO {

    private Long gasSalesReportDetailId;

    private Long gasSalesReportMonthlyId;

    private Long fuelTypeId;

    private String fuelName;

    private BigDecimal volumeSold;

    private BigDecimal profitPerGallon;
}