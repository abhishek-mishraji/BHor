package com.handsofretail.hor.service;

import com.handsofretail.hor.entity.FuelType;

import java.util.List;

public interface FuelTypeService {

    FuelType createFuelType(String fuelName);

    FuelType updateFuelType(Long fuelTypeId, String fuelName, Boolean active);

    void deleteFuelType(Long fuelTypeId);

    FuelType getFuelType(Long fuelTypeId);

    List<FuelType> getAllFuelTypes();

    List<FuelType> getActiveFuelTypes();
}