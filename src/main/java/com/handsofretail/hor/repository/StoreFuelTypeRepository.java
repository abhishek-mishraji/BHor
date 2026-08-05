package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.StoreFuelType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreFuelTypeRepository extends JpaRepository<StoreFuelType, Long> {

    List<StoreFuelType> findByStoreStoreIdAndActiveTrue(Long storeId);

    List<StoreFuelType> findByStoreStoreId(Long storeId);

    boolean existsByStoreStoreIdAndFuelTypeFuelTypeId(Long storeId, Long fuelTypeId);

    Optional<StoreFuelType> findByStoreStoreIdAndFuelTypeFuelTypeId(Long storeId, Long fuelTypeId);

    void deleteByStoreStoreId(Long storeId);
}