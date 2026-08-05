package com.handsofretail.hor.service;

import com.handsofretail.hor.entity.StoreFuelType;

import java.util.List;

public interface StoreFuelTypeService {

    StoreFuelType assignFuelType(Long storeId, Long fuelTypeId);

    List<StoreFuelType> replaceFuelTypes(Long storeId, List<Long> fuelTypeIds);

    List<StoreFuelType> getFuelTypesByStore(Long storeId);

    void removeFuelType(Long storeId, Long fuelTypeId);
}