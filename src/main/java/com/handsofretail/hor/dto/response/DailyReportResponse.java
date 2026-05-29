package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class DailyReportResponse {

    private Long dailyReportId;

    private Long storeId;

    private String storeName;

    private LocalDate reportDate;

    private BigDecimal groceryTotal;

    private BigDecimal volume;

    private BigDecimal cashDeposit;

    private BigDecimal checkDeposit;

    private BigDecimal overShort;
}