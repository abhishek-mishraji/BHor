package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class LotterySalesReportMonthlyResponse {

    private Long lotterySalesReportMonthlyId;
    private Long storeId;
    private Integer reportMonth;
    private Integer reportYear;
    private BigDecimal onlineSales;
    private BigDecimal scratchOffSales;
    private BigDecimal onlineCashes;
    private BigDecimal scratchOffCashes;
    private BigDecimal commission;
}