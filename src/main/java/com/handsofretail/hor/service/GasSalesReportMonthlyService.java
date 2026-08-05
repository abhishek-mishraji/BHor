package com.handsofretail.hor.service;

import com.handsofretail.hor.entity.GasSalesReportDetail;
import com.handsofretail.hor.entity.GasSalesReportMonthly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface GasSalesReportMonthlyService {

    GasSalesReportMonthly createReport(
            Long storeId,
            Integer reportMonth,
            Integer reportYear,
            BigDecimal creditFees,
            BigDecimal totalVolumeSold,
            BigDecimal netProfitPerGallon,
            BigDecimal netProfit,
            List<GasSalesReportDetail> details);

    GasSalesReportMonthly updateReport(
            Long reportId,
            BigDecimal creditFees,
            BigDecimal totalVolumeSold,
            BigDecimal netProfitPerGallon,
            BigDecimal netProfit,
            List<GasSalesReportDetail> details);

    void deleteReport(Long reportId);

    GasSalesReportMonthly getReportById(Long reportId);

    GasSalesReportMonthly getReportByStoreAndPeriod(Long storeId, Integer reportMonth, Integer reportYear);

    Page<GasSalesReportMonthly> listReports(Long storeId, Pageable pageable);

    List<GasSalesReportMonthly> listReportsByPeriod(Integer reportMonth, Integer reportYear);
}
