package com.handsofretail.hor.controller;

import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.StoreFuelTypeResponseDTO;
import com.handsofretail.hor.mapper.StoreFuelTypeMapper;
import com.handsofretail.hor.service.StoreFuelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("storeFuelTypeQueryController")
@RequestMapping("/api/v1/admin/stores/{storeId}/fuel-types")
@RequiredArgsConstructor
public class StoreFuelTypeController {

    private final StoreFuelTypeService storeFuelTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StoreFuelTypeResponseDTO>>> getFuelTypes(
            @PathVariable Long storeId) {
        List<StoreFuelTypeResponseDTO> response = storeFuelTypeService.getFuelTypesByStore(storeId).stream()
                .map(StoreFuelTypeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Store fuel types fetched", response));
    }
}
