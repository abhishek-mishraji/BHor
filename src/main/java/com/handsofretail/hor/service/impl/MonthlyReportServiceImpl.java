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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

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

                try {
                        byte[] fileContent = file.getBytes();

                        if (isHtmlFile(fileContent)) {
                                reports.addAll(parseHtmlMonthlyReport(
                                                fileContent,
                                                store,
                                                reportMonth,
                                                reportYear));
                        } else {
                                reports.addAll(parseExcelMonthlyReport(
                                                fileContent,
                                                store,
                                                reportMonth,
                                                reportYear));
                        }
                } catch (IOException | RuntimeException ex) {
                        throw new BadRequestException(
                                        "Unable to read monthly report file: " + ex.getMessage(),
                                        ex);
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

        private static boolean isHtmlFile(byte[] fileContent) {
                int inspectionLength = Math.min(fileContent.length, 4096);

                String beginning = new String(
                                fileContent,
                                0,
                                inspectionLength,
                                StandardCharsets.ISO_8859_1)
                                .trim()
                                .toLowerCase(Locale.ROOT);

                return beginning.startsWith("<!doctype html")
                                || beginning.startsWith("<html")
                                || beginning.startsWith("<table")
                                || beginning.contains("<html")
                                || beginning.contains("<table");
        }

        private List<MonthlyReport> parseExcelMonthlyReport(
                        byte[] fileContent,
                        Store store,
                        Integer reportMonth,
                        Integer reportYear) throws IOException {

                List<MonthlyReport> reports = new ArrayList<>();

                try (InputStream inputStream = new ByteArrayInputStream(fileContent);
                                Workbook workbook = WorkbookFactory.create(inputStream)) {

                        Sheet sheet = workbook.getSheetAt(0);
                        if (sheet == null) {
                                throw new BadRequestException("Excel sheet is missing");
                        }

                        int headerRowIndex = findExcelHeaderRowIndex(sheet);

                        for (int rowIndex = headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                                Row row = sheet.getRow(rowIndex);

                                if (row == null || isRowEmpty(row)) {
                                        continue;
                                }

                                String department = getCellString(row.getCell(0));

                                if (department == null
                                                || department.isBlank()
                                                || isSummaryRowLabel(department)) {
                                        continue;
                                }

                                reports.add(MonthlyReport.builder()
                                                .store(store)
                                                .reportMonth(reportMonth)
                                                .reportYear(reportYear)
                                                .departmentId(getRequiredString(row.getCell(1), rowIndex, "Dept ID"))
                                                .departmentName(department)
                                                .gross(getRequiredDecimal(row.getCell(2), rowIndex, "Gross"))
                                                .discount(getRequiredDecimal(row.getCell(3), rowIndex, "Discount"))
                                                .promotion(getRequiredDecimal(row.getCell(4), rowIndex, "Promotion"))
                                                .refund(getRequiredDecimal(row.getCell(5), rowIndex, "Refund"))
                                                .voidAmount(getRequiredDecimal(row.getCell(6), rowIndex, "Void"))
                                                .netSales(getRequiredDecimal(row.getCell(7), rowIndex, "Net Sales"))
                                                .build());
                        }
                }

                return reports;
        }

        private List<MonthlyReport> parseHtmlMonthlyReport(
                        byte[] fileContent,
                        Store store,
                        Integer reportMonth,
                        Integer reportYear) throws IOException {

                Document document = Jsoup.parse(
                                new ByteArrayInputStream(fileContent),
                                null,
                                "");

                Element table = document.selectFirst("table");
                if (table == null) {
                        throw new BadRequestException("Downloaded file is HTML but does not contain a table");
                }

                List<Element> rows = table.select("tr");
                if (rows.isEmpty()) {
                        throw new BadRequestException("Downloaded HTML table has no rows");
                }

                int headerRowIndex = findHtmlHeaderRowIndex(rows);

                List<MonthlyReport> reports = new ArrayList<>();

                for (int rowIndex = headerRowIndex + 1; rowIndex < rows.size(); rowIndex++) {
                        List<Element> cells = rows.get(rowIndex).children().stream()
                                        .filter(cell -> cell.tagName().equalsIgnoreCase("th")
                                                        || cell.tagName().equalsIgnoreCase("td"))
                                        .toList();

                        if (cells.isEmpty()) {
                                continue;
                        }

                        String department = cells.get(0).text();

                        if (department == null
                                        || department.isBlank()
                                        || isSummaryRowLabel(department)) {
                                continue;
                        }

                        if (cells.size() < EXPECTED_HEADERS.size()) {
                                throw new BadRequestException(
                                                "Row " + (rowIndex + 1) + ": expected 8 columns");
                        }

                        reports.add(MonthlyReport.builder()
                                        .store(store)
                                        .reportMonth(reportMonth)
                                        .reportYear(reportYear)
                                        .departmentName(department)
                                        .departmentId(getRequiredHtmlString(cells.get(1).text(), rowIndex, "Dept ID"))
                                        .gross(getRequiredHtmlDecimal(cells.get(2).text(), rowIndex, "Gross"))
                                        .discount(getRequiredHtmlDecimal(cells.get(3).text(), rowIndex, "Discount"))
                                        .promotion(getRequiredHtmlDecimal(cells.get(4).text(), rowIndex, "Promotion"))
                                        .refund(getRequiredHtmlDecimal(cells.get(5).text(), rowIndex, "Refund"))
                                        .voidAmount(getRequiredHtmlDecimal(cells.get(6).text(), rowIndex, "Void"))
                                        .netSales(getRequiredHtmlDecimal(cells.get(7).text(), rowIndex, "Net Sales"))
                                        .build());
                }

                return reports;
        }

        private static void validateHtmlHeaderRow(Element headerRow) {
                List<Element> headerCells = headerRow.children().stream()
                                .filter(cell -> cell.tagName().equalsIgnoreCase("th")
                                                || cell.tagName().equalsIgnoreCase("td"))
                                .toList();

                for (int columnIndex = 0; columnIndex < EXPECTED_HEADERS.size(); columnIndex++) {
                        String actual = columnIndex < headerCells.size()
                                        ? normalizeHeader(headerCells.get(columnIndex).text())
                                        : "";

                        String expected = EXPECTED_HEADERS.get(columnIndex);

                        if (!expected.equals(actual)) {
                                throw new BadRequestException(
                                                "Header mismatch at column " + (columnIndex + 1)
                                                                + ": expected '" + expected + "'");
                        }
                }
        }

        private static String getRequiredHtmlString(
                        String value,
                        int rowIndex,
                        String fieldName) {

                if (value == null || value.isBlank()) {
                        throw new BadRequestException(
                                        "Row " + (rowIndex + 1) + ": " + fieldName + " is required");
                }

                return value.trim();
        }

        private static BigDecimal getRequiredHtmlDecimal(
                        String value,
                        int rowIndex,
                        String fieldName) {

                return parseRequiredDecimal(value, rowIndex, fieldName);
        }

        private static int findExcelHeaderRowIndex(Sheet sheet) {
                int lastRowToCheck = Math.min(2, sheet.getLastRowNum());

                for (int rowIndex = 0; rowIndex <= lastRowToCheck; rowIndex++) {
                        Row row = sheet.getRow(rowIndex);

                        if (row != null && isExpectedExcelHeaderRow(row)) {
                                return rowIndex;
                        }
                }

                throw new BadRequestException(
                                "Required header row was not found in the first 3 rows");
        }

        private static boolean isExpectedExcelHeaderRow(Row row) {
                for (int columnIndex = 0; columnIndex < EXPECTED_HEADERS.size(); columnIndex++) {
                        String actual = normalizeHeader(getCellString(row.getCell(columnIndex)));
                        String expected = EXPECTED_HEADERS.get(columnIndex);

                        if (!expected.equals(actual)) {
                                return false;
                        }
                }

                return true;
        }

        private static int findHtmlHeaderRowIndex(List<Element> rows) {
                int lastRowToCheck = Math.min(2, rows.size() - 1);

                for (int rowIndex = 0; rowIndex <= lastRowToCheck; rowIndex++) {
                        if (isExpectedHtmlHeaderRow(rows.get(rowIndex))) {
                                return rowIndex;
                        }
                }

                throw new BadRequestException(
                                "Required header row was not found in the first 3 rows");
        }

        private static boolean isExpectedHtmlHeaderRow(Element row) {
                List<Element> cells = row.children().stream()
                                .filter(cell -> cell.tagName().equalsIgnoreCase("th")
                                                || cell.tagName().equalsIgnoreCase("td"))
                                .toList();

                if (cells.size() < EXPECTED_HEADERS.size()) {
                        return false;
                }

                for (int columnIndex = 0; columnIndex < EXPECTED_HEADERS.size(); columnIndex++) {
                        String actual = normalizeHeader(cells.get(columnIndex).text());
                        String expected = EXPECTED_HEADERS.get(columnIndex);

                        if (!expected.equals(actual)) {
                                return false;
                        }
                }

                return true;
        }

        private static boolean isSummaryRowLabel(String value) {
                if (value == null || value.isBlank()) {
                        return false;
                }

                String label = normalizeHeader(value).replaceAll("\\s+", " ");

                return label.matches(
                                "^(grand\\s+total|sub\\s*total|total|totals|average|avg|summary)\\b.*");
        }

        private static void validateFilename(String filename, Integer reportMonth, Integer reportYear) {
                if (filename == null) {
                        throw new BadRequestException("Uploaded file name is missing");
                }
                java.util.regex.Matcher matcher = java.util.regex.Pattern
                                .compile(
                                                "^monthly_(\\d{1,2})_(\\d{4})\\.(xlsx|xls)$",
                                                java.util.regex.Pattern.CASE_INSENSITIVE)
                                .matcher(filename);
                if (!matcher.matches()) {
                        throw new BadRequestException(
                                        "Uploaded file name does not match report month and year. Expected: monthly_"
                                                        + reportMonth + "_" + reportYear + ".xlsx or .xls");
                }
                int fileMonth = Integer.parseInt(matcher.group(1));
                int fileYear = Integer.parseInt(matcher.group(2));
                if (fileMonth != reportMonth || fileYear != reportYear) {
                        throw new BadRequestException(
                                        "Uploaded file name does not match report month and year. Expected: monthly_"
                                                        + reportMonth + "_" + reportYear + ".xlsx or .xls");
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

        private static BigDecimal parseRequiredDecimal(
                        String value,
                        int rowIndex,
                        String fieldName) {

                if (value == null || value.isBlank()) {
                        throw new BadRequestException(
                                        "Row " + (rowIndex + 1) + ": " + fieldName + " is required");
                }

                String normalized = value
                                .replace("\u00A0", "")
                                .replace(" ", "")
                                .trim();

                boolean isNegativeAccountingValue = normalized.startsWith("(")
                                && normalized.endsWith(")");

                if (normalized.startsWith("(") != normalized.endsWith(")")) {
                        throw new BadRequestException(
                                        "Row " + (rowIndex + 1) + ": " + fieldName + " has an invalid number format");
                }

                if (isNegativeAccountingValue) {
                        normalized = normalized.substring(1, normalized.length() - 1);
                }

                normalized = normalized
                                .replace("$", "")
                                .replace(",", "");

                if (!normalized.matches("-?\\d+(\\.\\d+)?")) {
                        throw new BadRequestException(
                                        "Row " + (rowIndex + 1) + ": " + fieldName + " must be a number");
                }

                BigDecimal amount = new BigDecimal(normalized);

                return isNegativeAccountingValue
                                ? amount.abs().negate()
                                : amount;
        }

        private static BigDecimal getRequiredDecimal(Cell cell, int rowIndex, String fieldName) {
                return parseRequiredDecimal(getCellString(cell), rowIndex, fieldName);
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