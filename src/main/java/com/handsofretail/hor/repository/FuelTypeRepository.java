package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {

    List<FuelType> findByActiveTrue();

    Optional<FuelType> findByFuelNameIgnoreCase(String fuelName);

    boolean existsByFuelNameIgnoreCase(String fuelName);

    boolean existsByFuelNameIgnoreCaseAndFuelTypeIdNot(String fuelName, Long fuelTypeId);
}