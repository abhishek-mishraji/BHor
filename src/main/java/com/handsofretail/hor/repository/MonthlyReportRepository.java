package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.MonthlyReport;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MonthlyReportRepository
                extends JpaRepository<MonthlyReport, Long>, JpaSpecificationExecutor<MonthlyReport> {

        List<MonthlyReport> findByStoreStoreId(Long storeId);

        long deleteByStoreStoreIdAndReportMonthAndReportYear(Long storeId, Integer reportMonth, Integer reportYear);
}
