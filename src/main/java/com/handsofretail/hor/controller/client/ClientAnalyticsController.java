package com.handsofretail.hor.controller.client;

import com.handsofretail.hor.dto.request.AnalyticsRequest;
import com.handsofretail.hor.dto.response.AnalyticsResponse;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.exception.ForbiddenException;
import com.handsofretail.hor.repository.ClientStoreMappingRepository;
import com.handsofretail.hor.security.user.CustomUserDetailsService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/client/analytics")
@RequiredArgsConstructor
public class ClientAnalyticsController {

    private final AnalyticsService analyticsService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ClientStoreMappingRepository clientStoreMappingRepository;

    @GetMapping("/reports")
    @Operation(
            summary = "Dynamic analytics query (client scope)",
            description = """
                    Same analytics capabilities as the admin endpoint but automatically scoped
                    to stores the authenticated client is a member of (OWNER or PARTNER).
                    storeIds may be passed to narrow the query to a subset of the client's own
                    stores; requesting a store outside the client's mappings returns 403.
                    clientId is ignored — access is always derived from the JWT.
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Analytics fetched"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid parameters or incompatible combination"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication required"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics(
            @Valid @ModelAttribute AnalyticsRequest request) {

        // Scope from JWT — client cannot query stores they don't belong to
        Long clientId = customUserDetailsService.getCurrentClient().getClientId();
        List<Long> allowedStoreIds = clientStoreMappingRepository
                .findByIdClientId(clientId)
                .stream()
                .map(m -> m.getId().getStoreId())
                .toList();

        List<Long> requestedStoreIds = request.getStoreIds();
        if (requestedStoreIds != null && !requestedStoreIds.isEmpty()) {
            if (!allowedStoreIds.containsAll(requestedStoreIds)) {
                throw new ForbiddenException("Access denied to one or more requested stores");
            }
            request.setStoreIds(requestedStoreIds);
        } else {
            request.setStoreIds(allowedStoreIds);
        }
        request.setClientId(null);

        AnalyticsResponse response = analyticsService.getAnalytics(request);
        return ResponseEntity.ok(ApiResponse.success("Analytics fetched", response));
    }
}
