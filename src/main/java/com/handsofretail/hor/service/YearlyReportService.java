package com.handsofretail.hor.service;

import java.util.List;

import com.handsofretail.hor.dto.request.YearlyReportRequest;
import com.handsofretail.hor.dto.request.YearlyReportUpdateRequest;
import com.handsofretail.hor.dto.response.YearlyReportResponse;

public interface YearlyReportService {

        YearlyReportResponse createYearlyReport(
                        YearlyReportRequest request);

        List<YearlyReportResponse> getYearlyReportsByStore(Long storeId);

        List<YearlyReportResponse> getYearlyReportsByStoreForClient(
                        Long storeId,
                        Long clientId);

        List<YearlyReportResponse> getYearlyReports(
                        Long storeId,
                        Long clientId,
                        Integer year);

        YearlyReportResponse updateYearlyReport(Long yearlyReportId, YearlyReportUpdateRequest request);

}