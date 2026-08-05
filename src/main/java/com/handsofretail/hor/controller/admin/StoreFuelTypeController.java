package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.StoreFuelTypeRequestDTO;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.StoreFuelTypeResponseDTO;
import com.handsofretail.hor.mapper.StoreFuelTypeMapper;
import com.handsofretail.hor.service.StoreFuelTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/stores/{storeId}/fuel-types")
@RequiredArgsConstructor
public class StoreFuelTypeController {

    private final StoreFuelTypeService storeFuelTypeService;

    @PutMapping
    public ResponseEntity<ApiResponse<List<StoreFuelTypeResponseDTO>>> replaceFuelTypes(
            @PathVariable Long storeId,
            @Valid @RequestBody StoreFuelTypeRequestDTO request) {
        List<StoreFuelTypeResponseDTO> response = storeFuelTypeService
                .replaceFuelTypes(storeId, request.getFuelTypeIds())
                .stream()
                .map(StoreFuelTypeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Store fuel types updated", response));
    }
}