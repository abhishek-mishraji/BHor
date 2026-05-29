package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.dto.request.DailyReportRequest;
import com.handsofretail.hor.dto.request.DailyReportUpdateRequest;
import com.handsofretail.hor.dto.response.DailyReportResponse;
import com.handsofretail.hor.entity.DailyReport;

import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.exception.ForbiddenException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.mapper.DailyReportMapper;
import com.handsofretail.hor.repository.DailyReportRepository;
import com.handsofretail.hor.repository.StoreRepository;
import com.handsofretail.hor.service.DailyReportService;
import com.handsofretail.hor.specification.DailyReportSpecification;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyReportServiceImpl
                implements DailyReportService {

        private final DailyReportRepository dailyReportRepository;

        private final StoreRepository storeRepository;

        @Override
        public List<DailyReportResponse> getDailyReportsByStore(Long storeId) {

                return dailyReportRepository
                                .findByStoreStoreId(storeId)
                                .stream()
                                .map(DailyReportMapper::toResponse)
                                .toList();
        }

        @Override
        public DailyReportResponse createDailyReport(
                        DailyReportRequest request) {

                Store store = storeRepository
                                .findById(request.getStoreId())
                                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

                DailyReport dailyReport = DailyReport.builder()
                                .store(store)
                                .reportDate(request.getReportDate())
                                .groceryTotal(request.getGroceryTotal())
                                .volume(request.getVolume())
                                .cashDeposit(request.getCashDeposit())
                                .checkDeposit(request.getCheckDeposit())
                                .overShort(request.getOverShort())
                                .build();

                DailyReport savedReport = dailyReportRepository
                                .save(dailyReport);

                return DailyReportMapper.toResponse(savedReport);
        }

        @Override
        public List<DailyReportResponse> getDailyReportsByStoreForClient(
                        Long storeId,
                        Long clientId) {

                Store store = storeRepository
                                .findById(storeId)
                                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

                if (!store.getClient().getClientId().equals(clientId)) {
                        throw new ForbiddenException("Access denied");
                }

                return getDailyReportsByStore(storeId);
        }

        @Override
        public List<DailyReportResponse> getDailyReports(
                        Long storeId,
                        Long clientId,
                        LocalDate fromDate,
                        LocalDate toDate) {

                Specification<DailyReport> spec = (root, query, cb) -> null;

                if (storeId != null) {
                        spec = spec.and(DailyReportSpecification.hasStoreId(storeId));
                }

                if (clientId != null) {
                        spec = spec.and(DailyReportSpecification.hasClientId(clientId));
                }

                if (fromDate != null && toDate != null) {
                        spec = spec.and(DailyReportSpecification.reportDateBetween(fromDate, toDate));
                } else if (fromDate != null) {
                        spec = spec.and(DailyReportSpecification.reportDateFrom(fromDate));
                } else if (toDate != null) {
                        spec = spec.and(DailyReportSpecification.reportDateTo(toDate));
                }

                return dailyReportRepository.findAll(spec)
                                .stream()
                                .map(DailyReportMapper::toResponse)
                                .toList();
        }

        @Override
        public DailyReportResponse updateDailyReport(Long dailyReportId, DailyReportUpdateRequest request) {

                DailyReport report = dailyReportRepository.findById(dailyReportId)
                                .orElseThrow(() -> new ResourceNotFoundException("Daily report not found"));

                if (request.getStoreId() != null
                                && !request.getStoreId().equals(report.getStore().getStoreId())) {

                        Store store = storeRepository.findById(request.getStoreId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
                        report.setStore(store);
                }

                if (request.getReportDate() != null) {
                        report.setReportDate(request.getReportDate());
                }

                if (request.getGroceryTotal() != null) {
                        report.setGroceryTotal(request.getGroceryTotal());
                }

                if (request.getVolume() != null) {
                        report.setVolume(request.getVolume());
                }

                if (request.getCashDeposit() != null) {
                        report.setCashDeposit(request.getCashDeposit());
                }

                if (request.getCheckDeposit() != null) {
                        report.setCheckDeposit(request.getCheckDeposit());
                }

                if (request.getOverShort() != null) {
                        report.setOverShort(request.getOverShort());
                }

                DailyReport saved = dailyReportRepository.save(report);
                return DailyReportMapper.toResponse(saved);
        }
}