package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.Store;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoreRepository
        extends JpaRepository<Store, Long>, JpaSpecificationExecutor<Store> {

    boolean existsByStoreCode(String storeCode);

    List<Store> findByClientClientId(Long clientId);
}