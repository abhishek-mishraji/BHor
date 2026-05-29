package com.handsofretail.hor.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YearlyReportUpdateRequest {

    private Long storeId;

    private Integer reportYear;

    private String annualSummary;
}