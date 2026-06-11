package com.handsofretail.hor.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MonthlyReportUpdateRequest {

    private Long storeId;

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