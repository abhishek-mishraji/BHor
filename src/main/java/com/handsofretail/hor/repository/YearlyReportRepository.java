package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.YearlyReport;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface YearlyReportRepository
                extends JpaRepository<YearlyReport, Long>, JpaSpecificationExecutor<YearlyReport> {

        List<YearlyReport> findByStoreStoreId(Long storeId);
}
