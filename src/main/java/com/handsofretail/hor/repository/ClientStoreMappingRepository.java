package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.ClientStoreId;
import com.handsofretail.hor.entity.ClientStoreMapping;
import com.handsofretail.hor.enums.StoreRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientStoreMappingRepository
        extends JpaRepository<ClientStoreMapping, ClientStoreId> {

    boolean existsByIdClientIdAndIdStoreId(Long clientId, Long storeId);

    List<ClientStoreMapping> findByIdStoreId(Long storeId);

    Optional<ClientStoreMapping> findByIdStoreIdAndRole(Long storeId, StoreRole role);

    List<ClientStoreMapping> findByIdClientId(Long clientId);

    void deleteByIdClientIdAndIdStoreId(Long clientId, Long storeId);

    @Query("""
            SELECT m FROM ClientStoreMapping m
            JOIN FETCH m.client c
            JOIN FETCH m.store s
            WHERE m.id.clientId = :clientId
            """)
    List<ClientStoreMapping> findMappingsWithDetailsByClientId(@Param("clientId") Long clientId);

    @Query("""
            SELECT m FROM ClientStoreMapping m
            JOIN FETCH m.client c
            WHERE m.id.storeId = :storeId AND m.role = :role
            """)
    Optional<ClientStoreMapping> findOwnerMappingByStoreId(
            @Param("storeId") Long storeId,
            @Param("role") StoreRole role);

    @Query("""
            SELECT m FROM ClientStoreMapping m
            JOIN FETCH m.client c
            WHERE m.id.storeId = :storeId
            ORDER BY m.role ASC
            """)
    List<ClientStoreMapping> findAllMembersWithClientByStoreId(@Param("storeId") Long storeId);
}
