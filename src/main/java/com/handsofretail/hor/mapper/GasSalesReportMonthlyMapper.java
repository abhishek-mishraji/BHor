package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.GasSalesReportMonthlyResponseDTO;
import com.handsofretail.hor.entity.GasSalesReportMonthly;

public final class GasSalesReportMonthlyMapper {

    private GasSalesReportMonthlyMapper() {
    }

    public static GasSalesReportMonthlyResponseDTO toResponse(GasSalesReportMonthly report) {
        return GasSalesReportMonthlyResponseDTO.builder()
                .gasSalesReportMonthlyId(report.getGasSalesReportMonthlyId())
                .storeId(report.getStore().getStoreId())
                .storeName(report.getStore().getStoreName())
                .reportMonth(report.getReportMonth())
                .reportYear(report.getReportYear())
                .creditFees(report.getCreditFees())
                .totalVolumeSold(report.getTotalVolumeSold())
                .netProfitPerGallon(report.getNetProfitPerGallon())
                .netProfit(report.getNetProfit())
                .details(report.getDetails().stream()
                        .map(GasSalesReportDetailMapper::toResponse)
                        .toList())
                .build();
    }
}