package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.LotterySalesReportMonthlyResponse;
import com.handsofretail.hor.entity.LotterySalesReportMonthly;

public final class LotterySalesReportMonthlyMapper {

    private LotterySalesReportMonthlyMapper() {
    }

    public static LotterySalesReportMonthlyResponse toResponse(LotterySalesReportMonthly report) {
        return LotterySalesReportMonthlyResponse.builder()
                .lotterySalesReportMonthlyId(report.getLotterySalesReportMonthlyId())
                .storeId(report.getStore().getStoreId())
                .reportMonth(report.getReportMonth())
                .reportYear(report.getReportYear())
                .onlineSales(report.getOnlineSales())
                .scratchOffSales(report.getScratchOffSales())
                .onlineCashes(report.getOnlineCashes())
                .scratchOffCashes(report.getScratchOffCashes())
                .commission(report.getCommission())
                .build();
    }
}