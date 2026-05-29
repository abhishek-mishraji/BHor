package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.MonthlyReportResponse;
import com.handsofretail.hor.entity.MonthlyReport;

public final class MonthlyReportMapper {

    private MonthlyReportMapper() {
    }

    public static MonthlyReportResponse toResponse(MonthlyReport report) {
        return MonthlyReportResponse.builder()
                .monthlyReportId(report.getMonthlyReportId())
                .storeId(report.getStore().getStoreId())
                .storeName(report.getStore().getStoreName())
                .reportMonth(report.getReportMonth())
                .reportYear(report.getReportYear())
                .departmentId(report.getDepartmentId())
                .departmentName(report.getDepartmentName())
                .gross(report.getGross())
                .discount(report.getDiscount())
                .promotion(report.getPromotion())
                .refund(report.getRefund())
                .voidAmount(report.getVoidAmount())
                .netSales(report.getNetSales())
                .build();
    }
}