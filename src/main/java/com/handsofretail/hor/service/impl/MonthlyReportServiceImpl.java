package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.dto.request.MonthlyReportRequest;
import com.handsofretail.hor.dto.request.MonthlyReportUpdateRequest;
import com.handsofretail.hor.dto.response.MonthlyReportResponse;
import com.handsofretail.hor.entity.MonthlyReport;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.exception.ForbiddenException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.mapper.MonthlyReportMapper;
import com.handsofretail.hor.repository.MonthlyReportRepository;
import com.handsofretail.hor.repository.StoreRepository;
import com.handsofretail.hor.service.MonthlyReportService;
import com.handsofretail.hor.specification.MonthlyReportSpecification;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonthlyReportServiceImpl
                implements MonthlyReportService {

        private final MonthlyReportRepository monthlyReportRepository;

        private final StoreRepository storeRepository;

        @Override
        public List<MonthlyReportResponse> getMonthlyReportsByStore(Long storeId) {

                return monthlyReportRepository
                                .findByStoreStoreId(storeId)
                                .stream()
                                .map(MonthlyReportMapper::toResponse)
                                .toList();
        }

        @Override
        public MonthlyReportResponse createMonthlyReport(
                        MonthlyReportRequest request) {

                Store store = storeRepository
                                .findById(request.getStoreId())
                                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

                MonthlyReport monthlyReport = MonthlyReport.builder()
                                .store(store)
                                .reportMonth(request.getReportMonth())
                                .reportYear(request.getReportYear())
                                .departmentId(request.getDepartmentId())
                                .departmentName(request.getDepartmentName())
                                .gross(request.getGross())
                                .discount(request.getDiscount())
                                .promotion(request.getPromotion())
                                .refund(request.getRefund())
                                .voidAmount(request.getVoidAmount())
                                .netSales(request.getNetSales())
                                .build();

                MonthlyReport savedReport = monthlyReportRepository
                                .save(monthlyReport);

                return MonthlyReportMapper.toResponse(savedReport);
        }

        @Override
        public List<MonthlyReportResponse> getMonthlyReportsByStoreForClient(
                        Long storeId,
                        Long clientId) {

                Store store = storeRepository
                                .findById(storeId)
                                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

                if (!store.getClient().getClientId().equals(clientId)) {
                        throw new ForbiddenException("Access denied");
                }

                return getMonthlyReportsByStore(storeId);
        }

        @Override
        public List<MonthlyReportResponse> getMonthlyReports(
                        Long storeId,
                        Long clientId,
                        Integer year,
                        Integer month) {

                Specification<MonthlyReport> spec = (root, query, cb) -> null;

                if (storeId != null) {
                        spec = spec.and(MonthlyReportSpecification.hasStoreId(storeId));
                }

                if (clientId != null) {
                        spec = spec.and(MonthlyReportSpecification.hasClientId(clientId));
                }

                if (year != null) {
                        spec = spec.and(MonthlyReportSpecification.hasYear(year));
                }

                if (month != null) {
                        spec = spec.and(MonthlyReportSpecification.hasMonth(month));
                }

                return monthlyReportRepository.findAll(spec)
                                .stream()
                                .map(MonthlyReportMapper::toResponse)
                                .toList();
        }

        @Override
        public MonthlyReportResponse updateMonthlyReport(Long monthlyReportId, MonthlyReportUpdateRequest request) {

                MonthlyReport report = monthlyReportRepository.findById(monthlyReportId)
                                .orElseThrow(() -> new ResourceNotFoundException("Monthly report not found"));

                if (request.getStoreId() != null
                                && !request.getStoreId().equals(report.getStore().getStoreId())) {

                        Store store = storeRepository.findById(request.getStoreId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
                        report.setStore(store);
                }

                if (request.getReportMonth() != null) {
                        report.setReportMonth(request.getReportMonth());
                }

                if (request.getReportYear() != null) {
                        report.setReportYear(request.getReportYear());
                }

                if (request.getDepartmentId() != null) {
                        report.setDepartmentId(request.getDepartmentId());
                }

                if (request.getDepartmentName() != null) {
                        report.setDepartmentName(request.getDepartmentName());
                }

                if (request.getGross() != null) {
                        report.setGross(request.getGross());
                }

                if (request.getDiscount() != null) {
                        report.setDiscount(request.getDiscount());
                }

                if (request.getPromotion() != null) {
                        report.setPromotion(request.getPromotion());
                }

                if (request.getRefund() != null) {
                        report.setRefund(request.getRefund());
                }

                if (request.getVoidAmount() != null) {
                        report.setVoidAmount(request.getVoidAmount());
                }

                if (request.getNetSales() != null) {
                        report.setNetSales(request.getNetSales());
                }

                MonthlyReport saved = monthlyReportRepository.save(report);
                return MonthlyReportMapper.toResponse(saved);
        }
}