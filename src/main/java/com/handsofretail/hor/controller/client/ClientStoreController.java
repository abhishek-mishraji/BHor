package com.handsofretail.hor.controller.client;

import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.StoreResponse;
import com.handsofretail.hor.security.user.CustomUserDetailsService;
import com.handsofretail.hor.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client/stores")
@RequiredArgsConstructor
public class ClientStoreController {

    private final StoreService storeService;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreResponse>>> getMyStores() {
        Long clientId = customUserDetailsService.getCurrentClient().getClientId();
        List<StoreResponse> response = storeService.getStoresByClientId(clientId);

        return ResponseEntity.ok(
                ApiResponse.success("Stores fetched", response));
    }
}