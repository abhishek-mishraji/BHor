package com.handsofretail.hor.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.handsofretail.hor.dto.request.MonthlyReportRequest;
import com.handsofretail.hor.dto.request.MonthlyReportUpdateRequest;
import com.handsofretail.hor.dto.response.MonthlyReportResponse;
import com.handsofretail.hor.dto.response.MonthlyReportUploadResponse;

public interface MonthlyReportService {

        MonthlyReportResponse createMonthlyReport(
                        MonthlyReportRequest request);

        List<MonthlyReportResponse> getMonthlyReportsByStore(Long storeId);

        List<MonthlyReportResponse> getMonthlyReportsByStoreForClient(
                        Long storeId,
                        Long clientId);

        List<MonthlyReportResponse> getMonthlyReports(
                        Long storeId,
                        Long clientId,
                        Integer year,
                        Integer month);

        MonthlyReportResponse updateMonthlyReport(Long monthlyReportId, MonthlyReportUpdateRequest request);

        MonthlyReportUploadResponse uploadMonthlyReportExcel(
                        Long storeId,
                        Integer reportMonth,
                        Integer reportYear,
                        MultipartFile file);
}