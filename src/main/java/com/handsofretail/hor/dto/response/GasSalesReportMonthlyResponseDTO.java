package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class GasSalesReportMonthlyResponseDTO {

    private Long gasSalesReportMonthlyId;

    private Long storeId;

    private String storeName;

    private Integer reportMonth;

    private Integer reportYear;

    private BigDecimal creditFees;

    private BigDecimal totalVolumeSold;

    private BigDecimal netProfitPerGallon;

    private BigDecimal netProfit;

    private List<GasSalesReportDetailResponseDTO> details;
}