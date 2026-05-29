package com.handsofretail.hor.service;

import java.time.LocalDate;
import java.util.List;

import com.handsofretail.hor.dto.request.DailyReportRequest;
import com.handsofretail.hor.dto.request.DailyReportUpdateRequest;
import com.handsofretail.hor.dto.response.DailyReportResponse;

public interface DailyReportService {

        DailyReportResponse createDailyReport(
                        DailyReportRequest request);

        List<DailyReportResponse> getDailyReportsByStore(Long storeId);

        List<DailyReportResponse> getDailyReportsByStoreForClient(
                        Long storeId,
                        Long clientId);

        List<DailyReportResponse> getDailyReports(
                        Long storeId,
                        Long clientId,
                        LocalDate fromDate,
                        LocalDate toDate);

        DailyReportResponse updateDailyReport(Long dailyReportId, DailyReportUpdateRequest request);
}