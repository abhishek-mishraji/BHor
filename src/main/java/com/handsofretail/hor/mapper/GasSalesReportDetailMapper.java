package com.handsofretail.hor.mapper;

import com.handsofretail.hor.dto.response.GasSalesReportDetailResponseDTO;
import com.handsofretail.hor.entity.GasSalesReportDetail;

public final class GasSalesReportDetailMapper {

    private GasSalesReportDetailMapper() {
    }

    public static GasSalesReportDetailResponseDTO toResponse(GasSalesReportDetail detail) {
        return GasSalesReportDetailResponseDTO.builder()
                .gasSalesReportDetailId(detail.getGasSalesReportDetailId())
                .gasSalesReportMonthlyId(detail.getGasSalesReportMonthly().getGasSalesReportMonthlyId())
                .fuelTypeId(detail.getFuelType().getFuelTypeId())
                .fuelName(detail.getFuelType().getFuelName())
                .volumeSold(detail.getVolumeSold())
                .profitPerGallon(detail.getProfitPerGallon())
                .build();
    }
}