package com.handsofretail.hor.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MonthlyReportRequest {

    @NotNull
    private Long storeId;

    @NotNull
    private Integer reportMonth;

    @NotNull
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