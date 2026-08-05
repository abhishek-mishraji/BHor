package com.handsofretail.hor.controller;

import com.handsofretail.hor.dto.response.ApiResponse;
import com.handsofretail.hor.dto.response.FuelTypeResponseDTO;
import com.handsofretail.hor.entity.FuelType;
import com.handsofretail.hor.mapper.FuelTypeMapper;
import com.handsofretail.hor.service.FuelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("fuelTypeQueryController")
@RequestMapping("/api/v1/admin/fuel-types")
@RequiredArgsConstructor
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FuelTypeResponseDTO>>> getFuelTypes() {
        List<FuelTypeResponseDTO> response = fuelTypeService.getActiveFuelTypes().stream()
                .map(FuelTypeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Fuel types fetched", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FuelTypeResponseDTO>> getFuelType(@PathVariable Long id) {
        FuelType fuelType = fuelTypeService.getFuelType(id);
        return ResponseEntity.ok(ApiResponse.success("Fuel type fetched", FuelTypeMapper.toResponse(fuelType)));
    }
}
