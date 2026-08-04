package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.dto.request.LotterySalesReportMonthlyRequest;
import com.handsofretail.hor.dto.response.LotterySalesReportMonthlyResponse;
import com.handsofretail.hor.entity.LotterySalesReportMonthly;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.exception.DuplicateResourceException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.mapper.LotterySalesReportMonthlyMapper;
import com.handsofretail.hor.repository.LotterySalesReportMonthlyRepository;
import com.handsofretail.hor.repository.StoreRepository;
import com.handsofretail.hor.service.LotterySalesReportMonthlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LotterySalesReportMonthlyServiceImpl implements LotterySalesReportMonthlyService {

    private final LotterySalesReportMonthlyRepository reportRepository;
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public LotterySalesReportMonthlyResponse createReport(LotterySalesReportMonthlyRequest request) {
        Store store = getStore(request.getStoreId());
        validateUniquePeriod(request.getStoreId(), request.getReportMonth(), request.getReportYear());

        LotterySalesReportMonthly report = newReport(request, store);
        return save(report);
    }

    @Override
    @Transactional
    public LotterySalesReportMonthlyResponse updateReport(Long reportId, LotterySalesReportMonthlyRequest request) {
        LotterySalesReportMonthly report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Lottery monthly sales report not found"));
        Store store = getStore(request.getStoreId());

        if (reportRepository.existsByStoreStoreIdAndReportMonthAndReportYearAndLotterySalesReportMonthlyIdNot(
                request.getStoreId(), request.getReportMonth(), request.getReportYear(), reportId)) {
            throw new DuplicateResourceException(
                    "Lottery monthly sales report already exists for this store and period");
        }

        report.setStore(store);
        report.setReportMonth(request.getReportMonth());
        report.setReportYear(request.getReportYear());
        report.setOnlineSales(request.getOnlineSales());
        report.setScratchOffSales(request.getScratchOffSales());
        report.setOnlineCashes(request.getOnlineCashes());
        report.setScratchOffCashes(request.getScratchOffCashes());
        report.setCommission(request.getCommission());
        return save(report);
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        LotterySalesReportMonthly report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Lottery monthly sales report not found"));
        reportRepository.delete(report);
    }

    @Override
    @Transactional(readOnly = true)
    public LotterySalesReportMonthlyResponse getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .map(LotterySalesReportMonthlyMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Lottery monthly sales report not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public LotterySalesReportMonthlyResponse getReportByStoreAndPeriod(
            Long storeId, Integer reportMonth, Integer reportYear) {
        getStore(storeId);
        return reportRepository.findByStoreIdAndReportMonthAndReportYear(storeId, reportMonth, reportYear)
                .map(LotterySalesReportMonthlyMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Lottery monthly sales report not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LotterySalesReportMonthlyResponse> listReports(
            Long storeId, Integer reportMonth, Integer reportYear) {
        List<LotterySalesReportMonthly> reports;
        if (storeId != null) {
            Store store = getStore(storeId);
            reports = reportMonth != null && reportYear != null
                    ? reportRepository.findByStoreIdAndReportMonthAndReportYear(storeId, reportMonth, reportYear)
                            .stream().toList()
                    : reportRepository.findAllByStore(store);
        } else {
            reports = reportRepository.findAll();
        }

        return reports.stream()
                .filter(report -> reportMonth == null || reportMonth.equals(report.getReportMonth()))
                .filter(report -> reportYear == null || reportYear.equals(report.getReportYear()))
                .map(LotterySalesReportMonthlyMapper::toResponse)
                .toList();
    }

    private Store getStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
    }

    private void validateUniquePeriod(Long storeId, Integer reportMonth, Integer reportYear) {
        if (reportRepository.existsByStoreIdAndReportMonthAndReportYear(storeId, reportMonth, reportYear)) {
            throw new DuplicateResourceException(
                    "Lottery monthly sales report already exists for this store and period");
        }
    }

    private LotterySalesReportMonthly newReport(
            LotterySalesReportMonthlyRequest request, Store store) {
        return LotterySalesReportMonthly.builder()
                .store(store)
                .reportMonth(request.getReportMonth())
                .reportYear(request.getReportYear())
                .onlineSales(request.getOnlineSales())
                .scratchOffSales(request.getScratchOffSales())
                .onlineCashes(request.getOnlineCashes())
                .scratchOffCashes(request.getScratchOffCashes())
                .commission(request.getCommission())
                .build();
    }

    private LotterySalesReportMonthlyResponse save(LotterySalesReportMonthly report) {
        try {
            return LotterySalesReportMonthlyMapper.toResponse(reportRepository.saveAndFlush(report));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException(
                    "Lottery monthly sales report already exists for this store and period", exception);
        }
    }
}