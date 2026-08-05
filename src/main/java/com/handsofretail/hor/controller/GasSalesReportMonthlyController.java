package com.handsofretail.hor.controller;

import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.GasSalesReportMonthlyResponseDTO;
import com.handsofretail.hor.entity.GasSalesReportMonthly;
import com.handsofretail.hor.mapper.GasSalesReportMonthlyMapper;
import com.handsofretail.hor.service.GasSalesReportMonthlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("gasSalesReportMonthlyQueryController")
@RequestMapping("/api/v1/gas-sales/monthly")
@RequiredArgsConstructor
public class GasSalesReportMonthlyController {

    private final GasSalesReportMonthlyService reportService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GasSalesReportMonthlyResponseDTO>> getReportById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Gas sales monthly report fetched",
                GasSalesReportMonthlyMapper.toResponse(reportService.getReportById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GasSalesReportMonthlyResponseDTO>>> listReports(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        List<GasSalesReportMonthly> reports;
        if (month != null && year != null) {
            reports = reportService.listReportsByPeriod(month, year);
        } else {
            reports = reportService.listReports(
                    storeId,
                    PageRequest.of(0, Integer.MAX_VALUE, Sort.by("reportYear").descending()
                            .and(Sort.by("reportMonth").descending())))
                    .getContent();
        }

        List<GasSalesReportMonthlyResponseDTO> response = reports.stream()
                .map(GasSalesReportMonthlyMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Gas sales monthly reports fetched", response));
    }
}