package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.MonthlyReportRequest;
import com.handsofretail.hor.dto.request.MonthlyReportUpdateRequest;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.MonthlyReportResponse;
import com.handsofretail.hor.service.MonthlyReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/monthly-reports")
@RequiredArgsConstructor
public class MonthlyReportController {

        private final MonthlyReportService monthlyReportService;

        @PostMapping
        public ResponseEntity<ApiResponse<MonthlyReportResponse>> createMonthlyReport(
                        @Valid @RequestBody MonthlyReportRequest request) {

                MonthlyReportResponse response = monthlyReportService.createMonthlyReport(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Monthly report created", response));
        }

        @GetMapping("/store/{storeId}")
        public ResponseEntity<ApiResponse<List<MonthlyReportResponse>>> getMonthlyReportsByStore(
                        @PathVariable Long storeId) {

                List<MonthlyReportResponse> response = monthlyReportService.getMonthlyReportsByStore(storeId);

                return ResponseEntity.ok(
                                ApiResponse.success("Monthly reports fetched", response));
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<MonthlyReportResponse>>> getMonthlyReports(
                        @RequestParam(required = false) Long storeId,
                        @RequestParam(required = false) Long clientId,
                        @RequestParam(required = false) Integer year,
                        @RequestParam(required = false) Integer month) {

                List<MonthlyReportResponse> response = monthlyReportService.getMonthlyReports(storeId, clientId, year,
                                month);

                return ResponseEntity.ok(
                                ApiResponse.success("Monthly reports fetched", response));
        }

        @PutMapping("/{monthlyReportId}")
        public ResponseEntity<ApiResponse<MonthlyReportResponse>> updateMonthlyReport(
                        @PathVariable Long monthlyReportId,
                        @Valid @RequestBody MonthlyReportUpdateRequest request) {

                MonthlyReportResponse response = monthlyReportService.updateMonthlyReport(monthlyReportId, request);
                return ResponseEntity.ok(ApiResponse.success("Monthly report updated", response));
        }
}