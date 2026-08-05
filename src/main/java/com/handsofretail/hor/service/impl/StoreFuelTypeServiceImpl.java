package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.entity.FuelType;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.entity.StoreFuelType;
import com.handsofretail.hor.exception.DuplicateResourceException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.repository.FuelTypeRepository;
import com.handsofretail.hor.repository.StoreFuelTypeRepository;
import com.handsofretail.hor.repository.StoreRepository;
import com.handsofretail.hor.service.StoreFuelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreFuelTypeServiceImpl implements StoreFuelTypeService {

    private final StoreRepository storeRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final StoreFuelTypeRepository storeFuelTypeRepository;

    @Override
    @Transactional
    public StoreFuelType assignFuelType(Long storeId, Long fuelTypeId) {
        Store store = getStore(storeId);
        FuelType fuelType = getFuelType(fuelTypeId);
        if (storeFuelTypeRepository.existsByStoreStoreIdAndFuelTypeFuelTypeId(storeId, fuelTypeId)) {
            throw new DuplicateResourceException("Fuel type is already assigned to this store");
        }

        return storeFuelTypeRepository.saveAndFlush(StoreFuelType.builder()
                .store(store)
                .fuelType(fuelType)
                .active(true)
                .build());
    }

    @Override
    @Transactional
    public List<StoreFuelType> replaceFuelTypes(Long storeId, List<Long> fuelTypeIds) {
        Store store = getStore(storeId);
        List<Long> uniqueFuelTypeIds = new ArrayList<>(new LinkedHashSet<>(
                fuelTypeIds == null ? List.of() : fuelTypeIds));
        Map<Long, FuelType> fuelTypesById = fuelTypeRepository.findAllById(uniqueFuelTypeIds).stream()
                .collect(Collectors.toMap(FuelType::getFuelTypeId, Function.identity()));

        if (fuelTypesById.size() != uniqueFuelTypeIds.size()) {
            Long missingFuelTypeId = uniqueFuelTypeIds.stream()
                    .filter(id -> !fuelTypesById.containsKey(id))
                    .findFirst()
                    .orElse(null);
            throw new ResourceNotFoundException("Fuel type not found: " + missingFuelTypeId);
        }

        storeFuelTypeRepository.deleteByStoreStoreId(storeId);
        // Flush orphaned mappings before inserting replacements. Otherwise Hibernate
        // may issue the INSERT first and violate the store/fuel unique constraint
        // when a mapping is retained in the replacement set.
        storeFuelTypeRepository.flush();
        List<StoreFuelType> mappings = uniqueFuelTypeIds.stream()
                .map(id -> StoreFuelType.builder()
                        .store(store)
                        .fuelType(fuelTypesById.get(id))
                        .active(true)
                        .build())
                .toList();
        return storeFuelTypeRepository.saveAllAndFlush(mappings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreFuelType> getFuelTypesByStore(Long storeId) {
        getStore(storeId);
        return storeFuelTypeRepository.findByStoreStoreId(storeId);
    }

    @Override
    @Transactional
    public void removeFuelType(Long storeId, Long fuelTypeId) {
        getStore(storeId);
        StoreFuelType mapping = storeFuelTypeRepository
                .findByStoreStoreIdAndFuelTypeFuelTypeId(storeId, fuelTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store fuel type mapping not found"));
        storeFuelTypeRepository.delete(mapping);
    }

    private Store getStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
    }

    private FuelType getFuelType(Long fuelTypeId) {
        return fuelTypeRepository.findById(fuelTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Fuel type not found"));
    }
}
