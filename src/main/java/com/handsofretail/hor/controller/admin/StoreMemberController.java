package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.StoreMemberRequest;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.StoreMemberResponse;
import com.handsofretail.hor.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/stores/{storeId}/members")
@RequiredArgsConstructor
public class StoreMemberController {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreMemberResponse>>> getStoreMembers(
            @PathVariable Long storeId) {

        List<StoreMemberResponse> response = storeService.getStoreMembers(storeId);
        return ResponseEntity.ok(
                ApiResponse.success("Store members fetched", response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StoreMemberResponse>> addStoreMember(
            @PathVariable Long storeId,
            @Valid @RequestBody StoreMemberRequest request) {

        StoreMemberResponse response = storeService.addStoreMember(storeId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Store member added", response));
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<ApiResponse<Void>> removeStoreMember(
            @PathVariable Long storeId,
            @PathVariable Long clientId) {

        storeService.removeStoreMember(storeId, clientId);
        return ResponseEntity.ok(
                ApiResponse.success("Store member removed", null));
    }
}
