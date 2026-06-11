package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class MonthlyReportResponse {

    private Long monthlyReportId;

    private Long storeId;

    private String storeName;

    private Integer reportMonth;

    private Integer reportYear;

    private String departmentId;

    private String departmentName;

    private BigDecimal gross;

    private BigDecimal discount;

    private BigDecimal promotion;
    private BigDecimal refund;

    private BigDecimal voidAmount;

    private BigDecimal netSales;
}