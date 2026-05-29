package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.DailyReportRequest;
import com.handsofretail.hor.dto.request.DailyReportUpdateRequest;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.DailyReportResponse;
import com.handsofretail.hor.service.DailyReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/daily-reports")
@RequiredArgsConstructor
public class DailyReportController {

        private final DailyReportService dailyReportService;

        @PostMapping
        public ResponseEntity<ApiResponse<DailyReportResponse>> createDailyReport(
                        @Valid @RequestBody DailyReportRequest request) {

                DailyReportResponse response = dailyReportService.createDailyReport(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Daily report created", response));
        }

        @GetMapping("/store/{storeId}")
        public ResponseEntity<ApiResponse<List<DailyReportResponse>>> getDailyReportsByStore(
                        @PathVariable Long storeId) {

                List<DailyReportResponse> response = dailyReportService.getDailyReportsByStore(storeId);

                return ResponseEntity.ok(
                                ApiResponse.success("Daily reports fetched", response));
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<DailyReportResponse>>> getDailyReports(
                        @RequestParam(required = false) Long storeId,
                        @RequestParam(required = false) Long clientId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

                List<DailyReportResponse> response = dailyReportService.getDailyReports(storeId, clientId, from, to);

                return ResponseEntity.ok(
                                ApiResponse.success("Daily reports fetched", response));
        }

        @PutMapping("/{dailyReportId}")
        public ResponseEntity<ApiResponse<DailyReportResponse>> updateDailyReport(
                        @PathVariable Long dailyReportId,
                        @Valid @RequestBody DailyReportUpdateRequest request) {

                DailyReportResponse response = dailyReportService.updateDailyReport(dailyReportId, request);
                return ResponseEntity.ok(ApiResponse.success("Daily report updated", response));
        }
}