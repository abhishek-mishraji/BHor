package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.AnalyticsRequest;
import com.handsofretail.hor.dto.response.AnalyticsResponse;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.exception.BadRequestException;
import com.handsofretail.hor.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/reports")
    @Operation(
            summary = "Dynamic analytics query",
            description = """
                    Flexible analytics endpoint supporting daily and monthly report aggregation.
                    Provide either storeIds[] or clientId (not both) to scope the query.
                    groupBy controls the X-axis bucket; metric[] controls which values are aggregated.
                    Response is Chart.js / Recharts ready (labels[] + datasets[]).
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analytics fetched"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid parameters or incompatible combination"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics(
            @Valid @ModelAttribute AnalyticsRequest request) {

        if (request.getStoreIds() != null && !request.getStoreIds().isEmpty()
                && request.getClientId() != null) {
            throw new BadRequestException("Provide either storeIds or clientId, not both");
        }

        if ((request.getStoreIds() == null || request.getStoreIds().isEmpty())
                && request.getClientId() == null) {
            throw new BadRequestException("Either storeIds or clientId must be provided");
        }

        AnalyticsResponse response = analyticsService.getAnalytics(request);
        return ResponseEntity.ok(ApiResponse.success("Analytics fetched", response));
    }
}
