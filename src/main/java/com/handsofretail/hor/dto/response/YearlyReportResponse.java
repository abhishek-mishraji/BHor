package com.handsofretail.hor.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class YearlyReportResponse {

    private Long yearlyReportId;

    private Long storeId;

    private String storeName;

    private Integer reportYear;

    private String annualSummary;
}