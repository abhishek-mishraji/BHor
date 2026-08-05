package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.GasSalesReportDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GasSalesReportDetailRepository extends JpaRepository<GasSalesReportDetail, Long> {

    List<GasSalesReportDetail> findByGasSalesReportMonthlyGasSalesReportMonthlyId(Long reportId);

    Optional<GasSalesReportDetail> findByGasSalesReportMonthlyGasSalesReportMonthlyIdAndFuelTypeFuelTypeId(
            Long reportId,
            Long fuelTypeId);

    boolean existsByGasSalesReportMonthlyGasSalesReportMonthlyIdAndFuelTypeFuelTypeId(
            Long reportId,
            Long fuelTypeId);

    void deleteByGasSalesReportMonthlyGasSalesReportMonthlyId(Long reportId);
}