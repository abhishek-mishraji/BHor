package com.handsofretail.hor.service;

import com.handsofretail.hor.dto.request.LotterySalesReportMonthlyRequest;
import com.handsofretail.hor.dto.response.LotterySalesReportMonthlyResponse;

import java.util.List;

public interface LotterySalesReportMonthlyService {

    LotterySalesReportMonthlyResponse createReport(LotterySalesReportMonthlyRequest request);

    LotterySalesReportMonthlyResponse updateReport(Long reportId, LotterySalesReportMonthlyRequest request);

    void deleteReport(Long reportId);

    LotterySalesReportMonthlyResponse getReportById(Long reportId);

    LotterySalesReportMonthlyResponse getReportByStoreAndPeriod(Long storeId, Integer reportMonth, Integer reportYear);

    List<LotterySalesReportMonthlyResponse> listReports(Long storeId, Integer reportMonth, Integer reportYear);
}