package com.handsofretail.hor.repository;

import com.handsofretail.hor.entity.DailyReport;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DailyReportRepository
                extends JpaRepository<DailyReport, Long>, JpaSpecificationExecutor<DailyReport> {

        List<DailyReport> findByStoreStoreId(Long storeId);
}
