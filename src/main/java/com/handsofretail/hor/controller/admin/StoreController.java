package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.StoreRequest;
import com.handsofretail.hor.dto.request.StoreUpdateRequest;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.StoreResponse;
import com.handsofretail.hor.enums.Status;
import com.handsofretail.hor.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/stores")
@RequiredArgsConstructor
public class StoreController {

        private final StoreService storeService;

        @PostMapping
        public ResponseEntity<ApiResponse<StoreResponse>> createStore(
                        @Valid @RequestBody StoreRequest request) {

                StoreResponse response = storeService.createStore(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("Store created", response));
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<StoreResponse>>> getStores(
                        @RequestParam(required = false) Long clientId,
                        @RequestParam(required = false) Status status) {

                List<StoreResponse> response = storeService.getStores(clientId, status);
                return ResponseEntity.ok(
                                ApiResponse.success("Stores fetched", response));
        }

        @GetMapping("/{storeId}")
        public ResponseEntity<ApiResponse<StoreResponse>> getStoreById(
                        @PathVariable Long storeId) {

                StoreResponse response = storeService.getStoreById(storeId);
                return ResponseEntity.ok(
                                ApiResponse.success("Store fetched", response));
        }

        @PatchMapping("/{storeId}/status")
        public ResponseEntity<ApiResponse<StoreResponse>> updateStoreStatus(
                        @PathVariable Long storeId,
                        @RequestParam Status status) {

                StoreResponse response = storeService.updateStoreStatus(storeId, status);
                return ResponseEntity.ok(
                                ApiResponse.success("Store status updated", response));
        }

        @PutMapping("/{storeId}")
        public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
                        @PathVariable Long storeId,
                        @Valid @RequestBody StoreUpdateRequest request) {

                StoreResponse response = storeService.updateStore(storeId, request);
                return ResponseEntity.ok(
                                ApiResponse.success("Store updated", response));
        }
}