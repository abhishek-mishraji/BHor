package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.LotterySalesReportMonthly;
import com.handsofretail.hor.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LotterySalesReportMonthlyRepository extends JpaRepository<LotterySalesReportMonthly, Long> {

    @Query("""
            select report from LotterySalesReportMonthly report
            where report.store.storeId = :storeId
              and report.reportMonth = :reportMonth
              and report.reportYear = :reportYear
            """)
    Optional<LotterySalesReportMonthly> findByStoreIdAndReportMonthAndReportYear(
            @Param("storeId") Long storeId,
            @Param("reportMonth") Integer reportMonth,
            @Param("reportYear") Integer reportYear);

    @Query("""
            select case when count(report) > 0 then true else false end
            from LotterySalesReportMonthly report
            where report.store.storeId = :storeId
              and report.reportMonth = :reportMonth
              and report.reportYear = :reportYear
            """)
    boolean existsByStoreIdAndReportMonthAndReportYear(
            @Param("storeId") Long storeId,
            @Param("reportMonth") Integer reportMonth,
            @Param("reportYear") Integer reportYear);

    boolean existsByStoreStoreIdAndReportMonthAndReportYearAndLotterySalesReportMonthlyIdNot(
            Long storeId, Integer reportMonth, Integer reportYear, Long reportId);

    List<LotterySalesReportMonthly> findAllByStore(Store store);

    List<LotterySalesReportMonthly> findAllByStoreStoreId(Long storeId);
}