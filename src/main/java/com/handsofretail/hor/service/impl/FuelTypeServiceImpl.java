package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.entity.FuelType;
import com.handsofretail.hor.exception.BadRequestException;
import com.handsofretail.hor.exception.DuplicateResourceException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.repository.FuelTypeRepository;
import com.handsofretail.hor.repository.GasSalesReportDetailRepository;
import com.handsofretail.hor.repository.StoreFuelTypeRepository;
import com.handsofretail.hor.service.FuelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuelTypeServiceImpl implements FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;
    private final StoreFuelTypeRepository storeFuelTypeRepository;
    private final GasSalesReportDetailRepository gasSalesReportDetailRepository;

    @Override
    @Transactional
    public FuelType createFuelType(String fuelName) {
        String normalizedName = normalizeName(fuelName);
        ensureUniqueName(normalizedName);

        try {
            return fuelTypeRepository.saveAndFlush(FuelType.builder()
                    .fuelName(normalizedName)
                    .active(true)
                    .build());
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException("Fuel type already exists", exception);
        }
    }

    @Override
    @Transactional
    public FuelType updateFuelType(Long fuelTypeId, String fuelName, Boolean active) {
        FuelType fuelType = getFuelType(fuelTypeId);
        String normalizedName = normalizeName(fuelName);

        if (fuelTypeRepository.existsByFuelNameIgnoreCaseAndFuelTypeIdNot(normalizedName, fuelTypeId)) {
            throw new DuplicateResourceException("Fuel type already exists");
        }

        fuelType.setFuelName(normalizedName);
        fuelType.setActive(active == null || active);

        try {
            return fuelTypeRepository.saveAndFlush(fuelType);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException("Fuel type already exists", exception);
        }
    }

    @Override
    @Transactional
    public void deleteFuelType(Long fuelTypeId) {
        FuelType fuelType = getFuelType(fuelTypeId);
        if (storeFuelTypeRepository.findAll().stream()
                .anyMatch(mapping -> mapping.getFuelType().getFuelTypeId().equals(fuelTypeId))) {
            throw new BadRequestException("Fuel type is assigned to one or more stores");
        }
        if (gasSalesReportDetailRepository.findAll().stream()
                .anyMatch(detail -> detail.getFuelType().getFuelTypeId().equals(fuelTypeId))) {
            throw new BadRequestException("Fuel type is used by one or more gas sales reports");
        }
        fuelTypeRepository.delete(fuelType);
    }

    @Override
    @Transactional(readOnly = true)
    public FuelType getFuelType(Long fuelTypeId) {
        return fuelTypeRepository.findById(fuelTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Fuel type not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuelType> getAllFuelTypes() {
        return fuelTypeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuelType> getActiveFuelTypes() {
        return fuelTypeRepository.findByActiveTrue();
    }

    private void ensureUniqueName(String fuelName) {
        if (fuelTypeRepository.existsByFuelNameIgnoreCase(fuelName)) {
            throw new DuplicateResourceException("Fuel type already exists");
        }
    }

    private String normalizeName(String fuelName) {
        if (fuelName == null || fuelName.isBlank()) {
            throw new BadRequestException("Fuel name is required");
        }
        return fuelName.trim();
    }
}