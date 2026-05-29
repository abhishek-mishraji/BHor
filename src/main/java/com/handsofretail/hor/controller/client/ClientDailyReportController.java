package com.handsofretail.hor.controller.client;

import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.DailyReportResponse;
import com.handsofretail.hor.security.user.CustomUserDetailsService;
import com.handsofretail.hor.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client/daily-reports")
@RequiredArgsConstructor
public class ClientDailyReportController {

    private final DailyReportService dailyReportService;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<List<DailyReportResponse>>> getDailyReportsByStore(
            @PathVariable Long storeId) {

        Long clientId = customUserDetailsService.getCurrentClient().getClientId();
        List<DailyReportResponse> response = dailyReportService.getDailyReportsByStoreForClient(storeId, clientId);

        return ResponseEntity.ok(
                ApiResponse.success("Daily reports fetched", response));
    }
}