package com.handsofretail.hor.controller;

import com.handsofretail.hor.dto.request.LotterySalesReportMonthlyRequest;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.LotterySalesReportMonthlyResponse;
import com.handsofretail.hor.service.LotterySalesReportMonthlyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lottery-sales/monthly")
@RequiredArgsConstructor
public class LotterySalesReportMonthlyController {

    private final LotterySalesReportMonthlyService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<LotterySalesReportMonthlyResponse>> createReport(
            @Valid @RequestBody LotterySalesReportMonthlyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lottery monthly sales report created", reportService.createReport(request)));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<LotterySalesReportMonthlyResponse>> getReportById(
            @PathVariable Long reportId) {
        return ResponseEntity.ok(ApiResponse.success("Lottery monthly sales report fetched",
                reportService.getReportById(reportId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LotterySalesReportMonthlyResponse>>> listReports(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(ApiResponse.success("Lottery monthly sales reports fetched",
                reportService.listReports(storeId, month, year)));
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<ApiResponse<LotterySalesReportMonthlyResponse>> updateReport(
            @PathVariable Long reportId,
            @Valid @RequestBody LotterySalesReportMonthlyRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Lottery monthly sales report updated",
                reportService.updateReport(reportId, request)));
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok(ApiResponse.success("Lottery monthly sales report deleted", null));
    }
}