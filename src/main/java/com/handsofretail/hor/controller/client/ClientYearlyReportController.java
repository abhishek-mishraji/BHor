package com.handsofretail.hor.controller.client;

import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.YearlyReportResponse;
import com.handsofretail.hor.security.user.CustomUserDetailsService;
import com.handsofretail.hor.service.YearlyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client/yearly-reports")
@RequiredArgsConstructor
public class ClientYearlyReportController {

    private final YearlyReportService yearlyReportService;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<List<YearlyReportResponse>>> getYearlyReportsByStore(
            @PathVariable Long storeId) {

        Long clientId = customUserDetailsService.getCurrentClient().getClientId();
        List<YearlyReportResponse> response = yearlyReportService.getYearlyReportsByStoreForClient(storeId, clientId);

        return ResponseEntity.ok(
                ApiResponse.success("Yearly reports fetched", response));
    }
}