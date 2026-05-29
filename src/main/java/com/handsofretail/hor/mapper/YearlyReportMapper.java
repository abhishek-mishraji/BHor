package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.YearlyReportResponse;
import com.handsofretail.hor.entity.YearlyReport;

public final class YearlyReportMapper {

    private YearlyReportMapper() {
    }

    public static YearlyReportResponse toResponse(YearlyReport report) {
        return YearlyReportResponse.builder()
                .yearlyReportId(report.getYearlyReportId())
                .storeId(report.getStore().getStoreId())
                .storeName(report.getStore().getStoreName())
                .reportYear(report.getReportYear())
                .annualSummary(report.getAnnualSummary())
                .build();
    }
}