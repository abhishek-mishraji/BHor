package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.dto.request.FuelTypeRequestDTO;
import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.FuelTypeResponseDTO;
import com.handsofretail.hor.entity.FuelType;
import com.handsofretail.hor.mapper.FuelTypeMapper;
import com.handsofretail.hor.service.FuelTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/fuel-types")
@RequiredArgsConstructor
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;

    @PostMapping
    public ResponseEntity<ApiResponse<FuelTypeResponseDTO>> createFuelType(
            @Valid @RequestBody FuelTypeRequestDTO request) {
        FuelType fuelType = fuelTypeService.createFuelType(request.getFuelName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fuel type created", FuelTypeMapper.toResponse(fuelType)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FuelTypeResponseDTO>> updateFuelType(
            @PathVariable Long id,
            @Valid @RequestBody FuelTypeRequestDTO request) {
        FuelType fuelType = fuelTypeService.updateFuelType(id, request.getFuelName(), request.getActive());
        return ResponseEntity.ok(ApiResponse.success("Fuel type updated", FuelTypeMapper.toResponse(fuelType)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFuelType(@PathVariable Long id) {
        fuelTypeService.deleteFuelType(id);
        return ResponseEntity.ok(ApiResponse.success("Fuel type deleted", null));
    }
}