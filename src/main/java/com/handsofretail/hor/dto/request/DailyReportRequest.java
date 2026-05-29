package com.handsofretail.hor.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DailyReportRequest {

    @NotNull
    private Long storeId;

    @NotNull
    private LocalDate reportDate;

    private BigDecimal groceryTotal;

    private BigDecimal volume;

    private BigDecimal cashDeposit;

    private BigDecimal checkDeposit;

    private BigDecimal overShort;
}