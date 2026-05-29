package com.handsofretail.hor.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YearlyReportRequest {

    @NotNull
    private Long storeId;

    @NotNull
    private Integer reportYear;

    private String annualSummary;
}