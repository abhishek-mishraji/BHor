package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.DailyReportResponse;
import com.handsofretail.hor.entity.DailyReport;

public final class DailyReportMapper {

    private DailyReportMapper() {
    }

    public static DailyReportResponse toResponse(DailyReport report) {
        return DailyReportResponse.builder()
                .dailyReportId(report.getDailyReportId())
                .storeId(report.getStore().getStoreId())
                .storeName(report.getStore().getStoreName())
                .reportDate(report.getReportDate())
                .groceryTotal(report.getGroceryTotal())
                .volume(report.getVolume())
                .cashDeposit(report.getCashDeposit())
                .checkDeposit(report.getCheckDeposit())
                .overShort(report.getOverShort())
                .noSale(report.getNoSale())
                .lineVoid(report.getLineVoid())
                .voidAmount(report.getVoidAmount())
                .refunds(report.getRefunds())
                .build();
    }
}