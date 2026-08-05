package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.GasSalesReportMonthly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GasSalesReportMonthlyRepository extends JpaRepository<GasSalesReportMonthly, Long> {

    Optional<GasSalesReportMonthly> findByStoreStoreIdAndReportMonthAndReportYear(
            Long storeId,
            Integer reportMonth,
            Integer reportYear);

    boolean existsByStoreStoreIdAndReportMonthAndReportYear(
            Long storeId,
            Integer reportMonth,
            Integer reportYear);

    Page<GasSalesReportMonthly> findByStoreStoreId(Long storeId, Pageable pageable);

    List<GasSalesReportMonthly> findByReportMonthAndReportYear(Integer reportMonth, Integer reportYear);
}