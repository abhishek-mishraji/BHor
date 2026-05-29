package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.YearlyReportRequest;
import com.handsofretail.hor.dto.request.YearlyReportUpdateRequest;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.YearlyReportResponse;
import com.handsofretail.hor.service.YearlyReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/yearly-reports")
@RequiredArgsConstructor
public class YearlyReportController {

        private final YearlyReportService yearlyReportService;

        @GetMapping("/store/{storeId}")
        public ResponseEntity<ApiResponse<List<YearlyReportResponse>>> getYearlyReportsByStore(
                        @PathVariable Long storeId) {

                List<YearlyReportResponse> response = yearlyReportService.getYearlyReportsByStore(storeId);

                return ResponseEntity.ok(
                                ApiResponse.success("Yearly reports fetched", response));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<YearlyReportResponse>> createYearlyReport(
                        @Valid @RequestBody YearlyReportRequest request) {

                YearlyReportResponse response = yearlyReportService.createYearlyReport(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Yearly report created", response));
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<YearlyReportResponse>>> getYearlyReports(
                        @RequestParam(required = false) Long storeId,
                        @RequestParam(required = false) Long clientId,
                        @RequestParam(required = false) Integer year) {

                List<YearlyReportResponse> response = yearlyReportService.getYearlyReports(storeId, clientId, year);

                return ResponseEntity.ok(
                                ApiResponse.success("Yearly reports fetched", response));
        }

        @PutMapping("/{yearlyReportId}")
        public ResponseEntity<ApiResponse<YearlyReportResponse>> updateYearlyReport(
                        @PathVariable Long yearlyReportId,
                        @Valid @RequestBody YearlyReportUpdateRequest request) {

                YearlyReportResponse response = yearlyReportService.updateYearlyReport(yearlyReportId, request);
                return ResponseEntity.ok(ApiResponse.success("Yearly report updated", response));
        }
}