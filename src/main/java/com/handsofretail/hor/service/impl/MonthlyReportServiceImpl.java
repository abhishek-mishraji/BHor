package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.dto.request.MonthlyReportRequest;
import com.handsofretail.hor.dto.request.MonthlyReportUpdateRequest;
import com.handsofretail.hor.dto.response.MonthlyReportResponse;
import com.handsofretail.hor.dto.response.MonthlyReportUploadResponse;
import com.handsofretail.hor.entity.MonthlyReport;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.exception.BadRequestException;
import com.handsofretail.hor.exception.ForbiddenException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.mapper.MonthlyReportMapper;
import com.handsofretail.hor.repository.ClientStoreMappingRepository;
import com.handsofretail.hor.repository.MonthlyReportRepository;
import com.handsofretail.hor.repository.StoreRepository;
import com.handsofretail.hor.service.MonthlyReportService;
import com.handsofretail.hor.specification.MonthlyReportSpecification;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MonthlyReportServiceImpl
                implements MonthlyReportService {

        private final MonthlyReportRepository monthlyReportRepository;
        private final StoreRepository storeRepository;
        private final ClientStoreMappingRepository clientStoreMappingRepository;
        private static final DataFormatter DATA_FORMATTER = new DataFormatter();

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

                if (!clientStoreMappingRepository.existsByIdClientIdAndIdStoreId(clientId, storeId)) {
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

        private static final List<String> EXPECTED_HEADERS = List.of(
                        "department",
                        "dept id",
                        "gross",
                        "discount",
                        "promotion",
                        "refund",
                        "void",
                        "net sales");

        @Override
        @Transactional
        public MonthlyReportUploadResponse uploadMonthlyReportExcel(
                        Long storeId,
                        Integer reportMonth,
                        Integer reportYear,
                        MultipartFile file) {

                if (storeId == null) {
                        throw new BadRequestException("Store ID is required");
                }

                if (reportMonth == null || reportMonth < 1 || reportMonth > 12) {
                        throw new BadRequestException("Report month must be between 1 and 12");
                }

                if (reportYear == null) {
                        throw new BadRequestException("Report year is required");
                }

                if (file == null || file.isEmpty()) {
                        throw new BadRequestException("Excel file is required");
                }

                String originalFilename = file.getOriginalFilename();
                validateFilename(originalFilename, reportMonth, reportYear);

                Store store = storeRepository
                                .findById(storeId)
                                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

                List<MonthlyReport> reports = new ArrayList<>();

                try (InputStream inputStream = file.getInputStream();
                                Workbook workbook = WorkbookFactory.create(inputStream)) {

                        Sheet sheet = workbook.getSheetAt(0);
                        if (sheet == null) {
                                throw new BadRequestException("Excel sheet is missing");
                        }

                        Row headerRow = sheet.getRow(0);
                        if (headerRow == null) {
                                throw new BadRequestException("Header row is missing");
                        }

                        validateHeaderRow(headerRow);

                        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                                Row row = sheet.getRow(rowIndex);
                                if (row == null || isRowEmpty(row)) {
                                        continue;
                                }

                                String department = getRequiredString(row.getCell(0), rowIndex, "Department");
                                String deptId = getRequiredString(row.getCell(1), rowIndex, "Dept ID");
                                BigDecimal gross = getRequiredDecimal(row.getCell(2), rowIndex, "Gross");
                                BigDecimal discount = getRequiredDecimal(row.getCell(3), rowIndex, "Discount");
                                BigDecimal promotion = getRequiredDecimal(row.getCell(4), rowIndex, "Promotion");
                                BigDecimal refund = getRequiredDecimal(row.getCell(5), rowIndex, "Refund");
                                BigDecimal voidAmount = getRequiredDecimal(row.getCell(6), rowIndex, "Void");
                                BigDecimal netSales = getRequiredDecimal(row.getCell(7), rowIndex, "Net Sales");

                                MonthlyReport report = MonthlyReport.builder()
                                                .store(store)
                                                .reportMonth(reportMonth)
                                                .reportYear(reportYear)
                                                .departmentId(deptId)
                                                .departmentName(department)
                                                .gross(gross)
                                                .discount(discount)
                                                .promotion(promotion)
                                                .refund(refund)
                                                .voidAmount(voidAmount)
                                                .netSales(netSales)
                                                .build();

                                reports.add(report);
                        }
                } catch (IOException ex) {
                        throw new BadRequestException("Unable to read Excel file", ex);
                }

                if (reports.isEmpty()) {
                        throw new BadRequestException("No data rows found in Excel file");
                }

                long deletedRows = monthlyReportRepository.deleteByStoreStoreIdAndReportMonthAndReportYear(
                                storeId,
                                reportMonth,
                                reportYear);

                monthlyReportRepository.saveAll(reports);

                return MonthlyReportUploadResponse.builder()
                                .totalRows(reports.size())
                                .insertedRows(reports.size())
                                .deletedRows(deletedRows)
                                .build();
        }

        private static void validateFilename(String filename, Integer reportMonth, Integer reportYear) {
                if (filename == null) {
                        throw new BadRequestException("Uploaded file name is missing");
                }
                java.util.regex.Matcher matcher = java.util.regex.Pattern
                                .compile("^monthly_(\\d{1,2})_(\\d{4})\\.xlsx$")
                                .matcher(filename);
                if (!matcher.matches()) {
                        throw new BadRequestException(
                                        "Uploaded file name does not match report month and year. Expected: monthly_"
                                                        + reportMonth + "_" + reportYear + ".xlsx");
                }
                int fileMonth = Integer.parseInt(matcher.group(1));
                int fileYear = Integer.parseInt(matcher.group(2));
                if (fileMonth != reportMonth || fileYear != reportYear) {
                        throw new BadRequestException(
                                        "Uploaded file name does not match report month and year. Expected: monthly_"
                                                        + reportMonth + "_" + reportYear + ".xlsx");
                }
        }

        private static void validateHeaderRow(Row headerRow) {
                for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
                        String actual = normalizeHeader(getCellString(headerRow.getCell(i)));
                        String expected = EXPECTED_HEADERS.get(i);

                        if (!expected.equals(actual)) {
                                throw new BadRequestException(
                                                "Header mismatch at column " + (i + 1) + ": expected '" + expected
                                                                + "'");
                        }
                }
        }

        private static boolean isRowEmpty(Row row) {
                for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
                        String value = getCellString(row.getCell(i));
                        if (value != null && !value.isBlank()) {
                                return false;
                        }
                }
                return true;
        }

        private static String getRequiredString(Cell cell, int rowIndex, String fieldName) {
                String value = getCellString(cell);
                if (value == null || value.isBlank()) {
                        throw new BadRequestException(
                                        "Row " + (rowIndex + 1) + ": " + fieldName + " is required");
                }
                return value;
        }

        private static Integer getRequiredInteger(Cell cell, int rowIndex, String fieldName) {
                String value = getCellString(cell);
                if (value == null || value.isBlank()) {
                        throw new BadRequestException(
                                        "Row " + (rowIndex + 1) + ": " + fieldName + " is required");
                }

                String normalized = value.replace(",", "").trim();
                try {
                        return new BigDecimal(normalized).intValueExact();
                } catch (NumberFormatException | ArithmeticException ex) {
                        throw new BadRequestException(
                                        "Row " + (rowIndex + 1) + ": " + fieldName + " must be an integer");
                }
        }

        private static BigDecimal getRequiredDecimal(Cell cell, int rowIndex, String fieldName) {
                String value = getCellString(cell);
                if (value == null || value.isBlank()) {
                        throw new BadRequestException(
                                        "Row " + (rowIndex + 1) + ": " + fieldName + " is required");
                }

                String normalized = value.replace("$", "").replace(",", "").trim();
                try {
                        return new BigDecimal(normalized);
                } catch (NumberFormatException ex) {
                        throw new BadRequestException(
                                        "Row " + (rowIndex + 1) + ": " + fieldName + " must be a number");
                }
        }

        private static String getCellString(Cell cell) {
                if (cell == null) {
                        return null;
                }

                String value = DATA_FORMATTER.formatCellValue(cell);
                if (value == null) {
                        return null;
                }

                value = value.trim();
                return value.isEmpty() ? null : value;
        }

        private static String normalizeHeader(String value) {
                if (value == null) {
                        return "";
                }
                return value.trim().toLowerCase(Locale.ROOT);
        }
}