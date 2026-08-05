package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.GasSalesReportDetailRequestDTO;
import com.handsofretail.hor.dto.request.GasSalesReportMonthlyCreateRequestDTO;
import com.handsofretail.hor.dto.request.GasSalesReportMonthlyUpdateRequestDTO;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.GasSalesReportMonthlyResponseDTO;
import com.handsofretail.hor.entity.FuelType;
import com.handsofretail.hor.entity.GasSalesReportDetail;
import com.handsofretail.hor.entity.GasSalesReportMonthly;
import com.handsofretail.hor.mapper.GasSalesReportMonthlyMapper;
import com.handsofretail.hor.service.GasSalesReportMonthlyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/gas-sales/monthly")
@RequiredArgsConstructor
public class GasSalesReportMonthlyController {

    private final GasSalesReportMonthlyService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<GasSalesReportMonthlyResponseDTO>> createReport(
            @Valid @RequestBody GasSalesReportMonthlyCreateRequestDTO request) {
        GasSalesReportMonthly report = reportService.createReport(
                request.getStoreId(),
                request.getReportMonth(),
                request.getReportYear(),
                request.getCreditFees(),
                request.getTotalVolumeSold(),
                request.getNetProfitPerGallon(),
                request.getNetProfit(),
                toDetails(request.getDetails()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Gas sales monthly report created",
                        GasSalesReportMonthlyMapper.toResponse(report)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GasSalesReportMonthlyResponseDTO>> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody GasSalesReportMonthlyUpdateRequestDTO request) {
        GasSalesReportMonthly report = reportService.updateReport(
                id,
                request.getCreditFees(),
                request.getTotalVolumeSold(),
                request.getNetProfitPerGallon(),
                request.getNetProfit(),
                toDetails(request.getDetails()));
        return ResponseEntity.ok(ApiResponse.success("Gas sales monthly report updated",
                GasSalesReportMonthlyMapper.toResponse(report)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok(ApiResponse.success("Gas sales monthly report deleted", null));
    }

    private List<GasSalesReportDetail> toDetails(List<GasSalesReportDetailRequestDTO> details) {
        return details.stream()
                .map(detail -> GasSalesReportDetail.builder()
                        .fuelType(FuelType.builder().fuelTypeId(detail.getFuelTypeId()).build())
                        .volumeSold(detail.getVolumeSold())
                        .profitPerGallon(detail.getProfitPerGallon())
                        .build())
                .toList();
    }
}
