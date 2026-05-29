package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.dto.request.YearlyReportRequest;
import com.handsofretail.hor.dto.request.YearlyReportUpdateRequest;
import com.handsofretail.hor.dto.response.YearlyReportResponse;

import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.entity.YearlyReport;
import com.handsofretail.hor.exception.ForbiddenException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.mapper.YearlyReportMapper;
import com.handsofretail.hor.repository.StoreRepository;
import com.handsofretail.hor.repository.YearlyReportRepository;
import com.handsofretail.hor.service.YearlyReportService;
import com.handsofretail.hor.specification.YearlyReportSpecification;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YearlyReportServiceImpl
                implements YearlyReportService {

        private final YearlyReportRepository yearlyReportRepository;

        private final StoreRepository storeRepository;

        @Override
        public List<YearlyReportResponse> getYearlyReportsByStore(Long storeId) {

                return yearlyReportRepository
                                .findByStoreStoreId(storeId)
                                .stream()
                                .map(YearlyReportMapper::toResponse)
                                .toList();
        }

        @Override
        public YearlyReportResponse createYearlyReport(
                        YearlyReportRequest request) {

                Store store = storeRepository
                                .findById(request.getStoreId())
                                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

                YearlyReport yearlyReport = YearlyReport.builder()
                                .store(store)
                                .reportYear(request.getReportYear())
                                .annualSummary(request.getAnnualSummary())
                                .build();

                YearlyReport savedReport = yearlyReportRepository
                                .save(yearlyReport);

                return YearlyReportMapper.toResponse(savedReport);
        }

        @Override
        public List<YearlyReportResponse> getYearlyReportsByStoreForClient(
                        Long storeId,
                        Long clientId) {

                Store store = storeRepository
                                .findById(storeId)
                                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

                if (!store.getClient().getClientId().equals(clientId)) {
                        throw new ForbiddenException("Access denied");
                }

                return getYearlyReportsByStore(storeId);
        }

        @Override
        public List<YearlyReportResponse> getYearlyReports(
                        Long storeId,
                        Long clientId,
                        Integer year) {

                Specification<YearlyReport> spec = (root, query, cb) -> null;

                if (storeId != null) {
                        spec = spec.and(YearlyReportSpecification.hasStoreId(storeId));
                }

                if (clientId != null) {
                        spec = spec.and(YearlyReportSpecification.hasClientId(clientId));
                }

                if (year != null) {
                        spec = spec.and(YearlyReportSpecification.hasYear(year));
                }

                return yearlyReportRepository.findAll(spec)
                                .stream()
                                .map(YearlyReportMapper::toResponse)
                                .toList();
        }

        @Override
        public YearlyReportResponse updateYearlyReport(Long yearlyReportId, YearlyReportUpdateRequest request) {

                YearlyReport report = yearlyReportRepository.findById(yearlyReportId)
                                .orElseThrow(() -> new ResourceNotFoundException("Yearly report not found"));

                if (request.getStoreId() != null
                                && !request.getStoreId().equals(report.getStore().getStoreId())) {

                        Store store = storeRepository.findById(request.getStoreId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
                        report.setStore(store);
                }

                if (request.getReportYear() != null) {
                        report.setReportYear(request.getReportYear());
                }

                if (request.getAnnualSummary() != null) {
                        report.setAnnualSummary(request.getAnnualSummary());
                }

                YearlyReport saved = yearlyReportRepository.save(report);
                return YearlyReportMapper.toResponse(saved);
        }
}