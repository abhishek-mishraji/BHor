package com.handsofretail.hor.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DailyReportUpdateRequest {

    private Long storeId;

    private LocalDate reportDate;

    private BigDecimal groceryTotal;

    private BigDecimal volume;

    private BigDecimal cashDeposit;

    private BigDecimal checkDeposit;

    private BigDecimal overShort;
}