package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.entity.FuelType;
import com.handsofretail.hor.entity.GasSalesReportDetail;
import com.handsofretail.hor.entity.GasSalesReportMonthly;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.entity.StoreFuelType;
import com.handsofretail.hor.exception.BadRequestException;
import com.handsofretail.hor.exception.DuplicateResourceException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.repository.GasSalesReportMonthlyRepository;
import com.handsofretail.hor.repository.StoreFuelTypeRepository;
import com.handsofretail.hor.repository.StoreRepository;
import com.handsofretail.hor.service.GasSalesReportMonthlyService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GasSalesReportMonthlyServiceImpl implements GasSalesReportMonthlyService {

    private final GasSalesReportMonthlyRepository reportRepository;
    private final StoreRepository storeRepository;
    private final StoreFuelTypeRepository storeFuelTypeRepository;

    @Override
    @Transactional
    public GasSalesReportMonthly createReport(
            Long storeId,
            Integer reportMonth,
            Integer reportYear,
            BigDecimal creditFees,
            BigDecimal totalVolumeSold,
            BigDecimal netProfitPerGallon,
            BigDecimal netProfit,
            List<GasSalesReportDetail> details) {
        Store store = getStore(storeId);
        validatePeriod(reportMonth, reportYear);
        validateCreditFees(creditFees);
        validateReportMetrics(totalVolumeSold, netProfitPerGallon, netProfit);
        if (reportRepository.existsByStoreStoreIdAndReportMonthAndReportYear(storeId, reportMonth, reportYear)) {
            throw new DuplicateResourceException("Gas sales report already exists for this store and period");
        }

        GasSalesReportMonthly report = GasSalesReportMonthly.builder()
                .store(store)
                .reportMonth(reportMonth)
                .reportYear(reportYear)
                .creditFees(creditFees)
                .totalVolumeSold(totalVolumeSold)
                .netProfitPerGallon(netProfitPerGallon)
                .netProfit(netProfit)
                .build();
        replaceDetails(report, details);
        return save(report);
    }

    @Override
    @Transactional
    public GasSalesReportMonthly updateReport(
            Long reportId,
            BigDecimal creditFees,
            BigDecimal totalVolumeSold,
            BigDecimal netProfitPerGallon,
            BigDecimal netProfit,
            List<GasSalesReportDetail> details) {
        GasSalesReportMonthly report = getReportById(reportId);
        validateCreditFees(creditFees);
        validateReportMetrics(totalVolumeSold, netProfitPerGallon, netProfit);
        report.setCreditFees(creditFees);
        report.setTotalVolumeSold(totalVolumeSold);
        report.setNetProfitPerGallon(netProfitPerGallon);
        report.setNetProfit(netProfit);
        replaceDetails(report, details);
        return save(report);
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        reportRepository.delete(getReportById(reportId));
    }

    @Override
    @Transactional(readOnly = true)
    public GasSalesReportMonthly getReportById(Long reportId) {
        GasSalesReportMonthly report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Gas sales report not found"));
        sortDetails(report);
        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public GasSalesReportMonthly getReportByStoreAndPeriod(
            Long storeId, Integer reportMonth, Integer reportYear) {
        getStore(storeId);
        GasSalesReportMonthly report = reportRepository
                .findByStoreStoreIdAndReportMonthAndReportYear(storeId, reportMonth, reportYear)
                .orElseThrow(() -> new ResourceNotFoundException("Gas sales report not found"));
        sortDetails(report);
        return report;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GasSalesReportMonthly> listReports(Long storeId, Pageable pageable) {
        Page<GasSalesReportMonthly> reports = reportRepository.findByStoreStoreId(storeId, pageable);
        reports.forEach(this::sortDetails);
        return reports;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GasSalesReportMonthly> listReportsByPeriod(Integer reportMonth, Integer reportYear) {
        List<GasSalesReportMonthly> reports = reportRepository.findByReportMonthAndReportYear(reportMonth, reportYear);
        reports.forEach(this::sortDetails);
        return reports;
    }

    private void replaceDetails(GasSalesReportMonthly report, List<GasSalesReportDetail> submittedDetails) {
        List<GasSalesReportDetail> details = submittedDetails == null ? List.of() : submittedDetails;
        Map<Long, FuelType> allowedFuelTypes = getAllowedFuelTypes(report.getStore().getStoreId());
        Set<Long> submittedFuelTypeIds = new HashSet<>();
        Map<Long, GasSalesReportDetail> existingDetailsByFuelTypeId = new HashMap<>();
        for (GasSalesReportDetail existingDetail : report.getDetails()) {
            existingDetailsByFuelTypeId.put(existingDetail.getFuelType().getFuelTypeId(), existingDetail);
        }

        for (GasSalesReportDetail submittedDetail : details) {
            validateDetail(submittedDetail);
            Long fuelTypeId = submittedDetail.getFuelType().getFuelTypeId();
            if (!submittedFuelTypeIds.add(fuelTypeId)) {
                throw new DuplicateResourceException("Fuel type is duplicated in the gas sales report");
            }

            FuelType fuelType = allowedFuelTypes.get(fuelTypeId);
            if (fuelType == null) {
                throw new BadRequestException("Fuel type is not configured for this store: " + fuelTypeId);
            }

            BigDecimal volumeSold = submittedDetail.getVolumeSold();
            BigDecimal profitPerGallon = submittedDetail.getProfitPerGallon();
            GasSalesReportDetail existingDetail = existingDetailsByFuelTypeId.get(fuelTypeId);
            if (existingDetail != null) {
                existingDetail.setVolumeSold(volumeSold);
                existingDetail.setProfitPerGallon(profitPerGallon);
            } else {
                GasSalesReportDetail detail = GasSalesReportDetail.builder()
                        .gasSalesReportMonthly(report)
                        .fuelType(fuelType)
                        .volumeSold(volumeSold)
                        .profitPerGallon(profitPerGallon)
                        .build();
                report.getDetails().add(detail);
            }
        }
        report.getDetails().removeIf(detail ->
                !submittedFuelTypeIds.contains(detail.getFuelType().getFuelTypeId()));
    }

    private Map<Long, FuelType> getAllowedFuelTypes(Long storeId) {
        Map<Long, FuelType> allowedFuelTypes = new HashMap<>();
        for (StoreFuelType mapping : storeFuelTypeRepository.findByStoreStoreIdAndActiveTrue(storeId)) {
            allowedFuelTypes.put(mapping.getFuelType().getFuelTypeId(), mapping.getFuelType());
        }
        return allowedFuelTypes;
    }

    private void validateDetail(GasSalesReportDetail detail) {
        if (detail == null || detail.getFuelType() == null || detail.getFuelType().getFuelTypeId() == null) {
            throw new BadRequestException("Fuel type is required for each gas sales detail");
        }
        if (detail.getVolumeSold() == null || detail.getVolumeSold().signum() < 0) {
            throw new BadRequestException("Volume sold must be zero or greater");
        }
        if (detail.getProfitPerGallon() == null || detail.getProfitPerGallon().signum() < 0) {
            throw new BadRequestException("Profit per gallon must be zero or greater");
        }
    }

    private void validateCreditFees(BigDecimal creditFees) {
        if (creditFees == null || creditFees.signum() < 0) {
            throw new BadRequestException("Credit fees must be zero or greater");
        }
    }

    private void validateReportMetrics(
            BigDecimal totalVolumeSold,
            BigDecimal netProfitPerGallon,
            BigDecimal netProfit) {
        if (totalVolumeSold == null || totalVolumeSold.signum() < 0) {
            throw new BadRequestException("Total volume sold must be zero or greater");
        }
        if (netProfitPerGallon == null || netProfitPerGallon.signum() < 0) {
            throw new BadRequestException("Net profit per gallon must be zero or greater");
        }
        if (netProfit == null || netProfit.signum() < 0) {
            throw new BadRequestException("Net profit must be zero or greater");
        }
    }

    private void validatePeriod(Integer reportMonth, Integer reportYear) {
        if (reportMonth == null || reportMonth < 1 || reportMonth > 12) {
            throw new BadRequestException("Report month must be between 1 and 12");
        }
        if (reportYear == null || reportYear < 1) {
            throw new BadRequestException("Report year must be greater than zero");
        }
    }

    private void sortDetails(GasSalesReportMonthly report) {
        report.getDetails().sort(Comparator.comparing(
                detail -> detail.getFuelType().getFuelName(),
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
    }

    private GasSalesReportMonthly save(GasSalesReportMonthly report) {
        try {
            return reportRepository.saveAndFlush(report);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException(
                    "Gas sales report already exists for this store and period", exception);
        }
    }

    private Store getStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
    }

}
