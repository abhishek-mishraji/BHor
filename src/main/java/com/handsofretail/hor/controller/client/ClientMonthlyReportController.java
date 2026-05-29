package com.handsofretail.hor.controller.client;

import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.MonthlyReportResponse;
import com.handsofretail.hor.security.user.CustomUserDetailsService;
import com.handsofretail.hor.service.MonthlyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client/monthly-reports")
@RequiredArgsConstructor
public class ClientMonthlyReportController {

    private final MonthlyReportService monthlyReportService;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<List<MonthlyReportResponse>>> getMonthlyReportsByStore(
            @PathVariable Long storeId) {

        Long clientId = customUserDetailsService.getCurrentClient().getClientId();
        List<MonthlyReportResponse> response = monthlyReportService.getMonthlyReportsByStoreForClient(storeId,
                clientId);

        return ResponseEntity.ok(
                ApiResponse.success("Monthly reports fetched", response));
    }
}