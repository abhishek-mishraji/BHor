package com.handsofretail.hor.service.impl;

import com.handsofretail.hor.dto.request.AnalyticsRequest;
import com.handsofretail.hor.dto.response.AnalyticsResponse;
import com.handsofretail.hor.dto.response.DatasetDto;
import com.handsofretail.hor.entity.DailyReport;
import com.handsofretail.hor.entity.GasSalesReportMonthly;
import com.handsofretail.hor.entity.LotterySalesReportMonthly;
import com.handsofretail.hor.entity.MonthlyReport;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.enums.AggregateType;
import com.handsofretail.hor.enums.GroupByField;
import com.handsofretail.hor.enums.ReportType;
import com.handsofretail.hor.exception.BadRequestException;
import com.handsofretail.hor.exception.ResourceNotFoundException;
import com.handsofretail.hor.repository.ClientStoreMappingRepository;
import com.handsofretail.hor.repository.ClientUserRepository;
import com.handsofretail.hor.repository.GasSalesReportMonthlyRepository;
import com.handsofretail.hor.repository.LotterySalesReportMonthlyRepository;
import com.handsofretail.hor.service.AnalyticsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final EntityManager entityManager;
    private final ClientStoreMappingRepository clientStoreMappingRepository;
    private final ClientUserRepository clientUserRepository;
    private final GasSalesReportMonthlyRepository gasSalesReportMonthlyRepository;
    private final LotterySalesReportMonthlyRepository lotterySalesReportMonthlyRepository;

    private static final Set<String> DAILY_METRICS = Set.of(
            "groceryTotal", "volume", "cashDeposit", "checkDeposit",
            "overShort", "noSale", "lineVoid", "voidAmount", "refunds");

    private static final Set<String> MONTHLY_METRICS = Set.of(
            "gross", "netSales", "discount", "promotion", "refund", "voidAmount");

    private static final List<String> GAS_MONTHLY_METRICS = List.of(
            "CREDIT_FEES", "TOTAL_VOLUME_SOLD", "NET_PROFIT", "NET_PROFIT_PER_GALLON");

    private static final List<String> LOTTERY_MONTHLY_METRICS = List.of(
            "ONLINE_SALES", "SCRATCH_OFF_SALES", "ONLINE_CASHES", "SCRATCH_OFF_CASHES", "COMMISSION");

    private static final Pattern GAS_DETAIL_METRIC_PATTERN = Pattern.compile(
            "^DETAIL_(VOLUME_SOLD|PROFIT)_([1-9]\\d*)$");

    private static final Set<GroupByField> DAILY_GROUP_BY = Set.of(GroupByField.DATE, GroupByField.STORE);

    private static final Set<GroupByField> MONTHLY_GROUP_BY = Set.of(
            GroupByField.MONTH, GroupByField.QUARTER, GroupByField.YEAR,
            GroupByField.STORE, GroupByField.DEPARTMENT);

    private static final Map<String, String> METRIC_LABELS = Map.ofEntries(
            Map.entry("groceryTotal", "Grocery Total"),
            Map.entry("volume", "Volume"),
            Map.entry("cashDeposit", "Cash Deposit"),
            Map.entry("checkDeposit", "Check Deposit"),
            Map.entry("overShort", "Over/Short"),
            Map.entry("noSale", "No Sale"),
            Map.entry("lineVoid", "Line Void"),
            Map.entry("voidAmount", "Void Amount"),
            Map.entry("refunds", "Refunds"),
            Map.entry("gross", "Gross"),
            Map.entry("netSales", "Net Sales"),
            Map.entry("discount", "Discount"),
            Map.entry("promotion", "Promotion"),
            Map.entry("refund", "Refund"),
            Map.entry("CREDIT_FEES", "Credit Fees"),
            Map.entry("TOTAL_VOLUME_SOLD", "Total Volume Sold"),
            Map.entry("NET_PROFIT", "Net Profit"),
            Map.entry("NET_PROFIT_PER_GALLON", "Net Profit Per Gallon"),
            Map.entry("ONLINE_SALES", "Online Sales"),
            Map.entry("SCRATCH_OFF_SALES", "Scratch Off Sales"),
            Map.entry("ONLINE_CASHES", "Online Cashes"),
            Map.entry("SCRATCH_OFF_CASHES", "Scratch Off Cashes"),
            Map.entry("COMMISSION", "Commission"));

    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics(AnalyticsRequest request) {
        validate(request);
        resolveClientIdToStoreIds(request);

        return switch (request.getReportType()) {
            case DAILY -> executeDailyAnalytics(request);
            case MONTHLY -> executeMonthlyAnalytics(request);
            case GAS_MONTHLY -> executeGasMonthlyAnalytics(request);
            case LOTTERY_MONTHLY -> executeLotteryMonthlyAnalytics(request);
        };
    }

    // ─── Validation ───────────────────────────────────────────────────────────

    private void validate(AnalyticsRequest request) {
        ReportType type = request.getReportType();
        GroupByField groupBy = request.getGroupBy();

        if (type == ReportType.GAS_MONTHLY) {
            validateGasMonthlyRequest(request);
            return;
        }
        if (type == ReportType.LOTTERY_MONTHLY) {
            validateLotteryMonthlyRequest(request);
            return;
        }

        Set<GroupByField> validGroupBy = type == ReportType.DAILY ? DAILY_GROUP_BY : MONTHLY_GROUP_BY;
        if (!validGroupBy.contains(groupBy)) {
            throw new BadRequestException(
                    "groupBy " + groupBy + " is not valid for " + type + " reports. " +
                            "Valid options: " + validGroupBy);
        }

        Set<String> validMetrics = type == ReportType.DAILY ? DAILY_METRICS : MONTHLY_METRICS;
        for (String metric : request.getMetric()) {
            if (!validMetrics.contains(metric)) {
                throw new BadRequestException(
                        "Metric '" + metric + "' is not valid for " + type + " reports. " +
                                "Valid metrics: " + validMetrics);
            }
        }

        if (groupBy == GroupByField.MONTH && isNullOrEmpty(request.getYear())) {
            throw new BadRequestException("year is required when groupBy=MONTH");
        }
        if (groupBy == GroupByField.QUARTER && isNullOrEmpty(request.getYear())) {
            throw new BadRequestException("year is required when groupBy=QUARTER");
        }
        if (groupBy == GroupByField.YEAR && isNullOrEmpty(request.getYear())) {
            throw new BadRequestException("year is required when groupBy=YEAR");
        }
        if (groupBy == GroupByField.DEPARTMENT) {
            if (isNullOrEmpty(request.getStoreIds())) {
                throw new BadRequestException("storeIds and year are required when groupBy=DEPARTMENT");
            }
            if (isNullOrEmpty(request.getYear())) {
                throw new BadRequestException("storeIds and year are required when groupBy=DEPARTMENT");
            }
        }

        if (request.getFrom() != null && request.getTo() != null
                && request.getFrom().isAfter(request.getTo())) {
            throw new BadRequestException("from date must be before to date");
        }

        if (request.getMonth() != null && (request.getMonth() < 1 || request.getMonth() > 12)) {
            throw new BadRequestException("month must be between 1 and 12");
        }
    }

    private void validateGasMonthlyRequest(AnalyticsRequest request) {
        if (request.getGroupBy() != GroupByField.MONTH) {
            throw new BadRequestException("groupBy=MONTH is required for GAS_MONTHLY reports");
        }
        if (isNullOrEmpty(request.getStoreIds()) || request.getStoreIds().size() != 1) {
            throw new BadRequestException("Exactly one storeId is required for GAS_MONTHLY reports");
        }
        if (request.getComparisonAMonth() == null || request.getComparisonAYear() == null
                || request.getComparisonBMonth() == null || request.getComparisonBYear() == null) {
            throw new BadRequestException("Both gas monthly comparison periods are required");
        }
        validateGasPeriod(request.getComparisonAMonth(), request.getComparisonAYear());
        validateGasPeriod(request.getComparisonBMonth(), request.getComparisonBYear());

        for (String metric : request.getMetric()) {
            if ("ALL".equalsIgnoreCase(metric)) {
                continue;
            }
            String normalizedMetric = normalizeGasMetric(metric);
            if (!GAS_MONTHLY_METRICS.contains(normalizedMetric)
                    && parseGasDetailMetric(normalizedMetric) == null) {
                throw new BadRequestException(
                        "Metric '" + metric + "' is not valid for GAS_MONTHLY reports. Valid metrics: "
                                + GAS_MONTHLY_METRICS
                                + ", DETAIL_VOLUME_SOLD_{fuelTypeId}, DETAIL_PROFIT_{fuelTypeId}");
            }
        }
    }

    private void validateGasPeriod(Integer month, Integer year) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("Gas report month must be between 1 and 12");
        }
        if (year < 1 || year > 9999) {
            throw new BadRequestException("Gas report year must be between 1 and 9999");
        }
    }

    private void validateLotteryMonthlyRequest(AnalyticsRequest request) {
        if (request.getGroupBy() != GroupByField.MONTH) {
            throw new BadRequestException("groupBy=MONTH is required for LOTTERY_MONTHLY reports");
        }
        if (isNullOrEmpty(request.getStoreIds()) || request.getStoreIds().size() != 1) {
            throw new BadRequestException("Exactly one storeId is required for LOTTERY_MONTHLY reports");
        }
        if (request.getComparisonAMonth() == null || request.getComparisonAYear() == null
                || request.getComparisonBMonth() == null || request.getComparisonBYear() == null) {
            throw new BadRequestException("Both lottery monthly comparison periods are required");
        }
        validateLotteryPeriod(request.getComparisonAMonth(), request.getComparisonAYear());
        validateLotteryPeriod(request.getComparisonBMonth(), request.getComparisonBYear());

        for (String metric : request.getMetric()) {
            if ("ALL".equalsIgnoreCase(metric)) {
                continue;
            }
            if (!LOTTERY_MONTHLY_METRICS.contains(normalizeLotteryMetric(metric))) {
                throw new BadRequestException(
                        "Metric '" + metric + "' is not valid for LOTTERY_MONTHLY reports. Valid metrics: "
                                + LOTTERY_MONTHLY_METRICS + ", ALL");
            }
        }
    }

    private void validateLotteryPeriod(Integer month, Integer year) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("Lottery report month must be between 1 and 12");
        }
        if (year < 1 || year > 9999) {
            throw new BadRequestException("Lottery report year must be between 1 and 9999");
        }
    }

    // Called only from admin context. Client endpoint pre-fills storeIds before
    // calling service.
    private void resolveClientIdToStoreIds(AnalyticsRequest request) {
        if (request.getClientId() == null)
            return;

        if (!clientUserRepository.existsById(request.getClientId())) {
            throw new ResourceNotFoundException("Client not found");
        }

        List<Long> storeIds = clientStoreMappingRepository
                .findByIdClientId(request.getClientId())
                .stream()
                .map(m -> m.getId().getStoreId())
                .toList();

        request.setStoreIds(storeIds);
        request.setClientId(null);
    }

    // ─── Daily Query ──────────────────────────────────────────────────────────

    private AnalyticsResponse executeDailyAnalytics(AnalyticsRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<DailyReport> root = q.from(DailyReport.class);
        Join<DailyReport, Store> storeJoin = root.join("store", JoinType.LEFT);

        Expression<?> groupExpr;
        Expression<?> labelExpr;
        List<Expression<?>> groupByList = new ArrayList<>();

        if (request.getGroupBy() == GroupByField.DATE) {
            groupExpr = root.get("reportDate");
            labelExpr = root.get("reportDate");
            groupByList.add(groupExpr);
        } else { // STORE
            groupExpr = storeJoin.get("storeId");
            labelExpr = storeJoin.get("storeName");
            groupByList.add(groupExpr);
            groupByList.add(labelExpr);
        }

        List<Predicate> predicates = buildDailyPredicates(cb, root, storeJoin, request);
        List<Selection<?>> selections = buildSelections(cb, root, labelExpr, request);

        q.multiselect(selections)
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(groupByList)
                .orderBy(cb.asc(groupExpr));

        List<Tuple> tuples = entityManager.createQuery(q).getResultList();
        return toResponse(tuples, request);
    }

    private List<Predicate> buildDailyPredicates(CriteriaBuilder cb, Root<DailyReport> root,
            Join<DailyReport, Store> storeJoin,
            AnalyticsRequest request) {
        List<Predicate> predicates = new ArrayList<>();
        if (!isNullOrEmpty(request.getStoreIds())) {
            predicates.add(storeJoin.get("storeId").in(request.getStoreIds()));
        }
        if (request.getFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("reportDate"), request.getFrom()));
        }
        if (request.getTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("reportDate"), request.getTo()));
        }
        return predicates;
    }

    // ─── Monthly Query ────────────────────────────────────────────────────────

    private AnalyticsResponse executeMonthlyAnalytics(AnalyticsRequest request) {
        // Multiple years with groupBy=MONTH need (year, month) buckets — a plain
        // reportMonth grouping would merge the same month of different years.
        if (request.getGroupBy() == GroupByField.MONTH && request.getYear().size() > 1) {
            return executeMultiYearMonthAnalytics(request);
        }
        if (request.getGroupBy() == GroupByField.QUARTER) {
            return executeQuarterAnalytics(request);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<MonthlyReport> root = q.from(MonthlyReport.class);
        Join<MonthlyReport, Store> storeJoin = root.join("store", JoinType.LEFT);

        Expression<?> groupExpr;
        Expression<?> labelExpr;
        List<Expression<?>> groupByList = new ArrayList<>();

        switch (request.getGroupBy()) {
            case MONTH -> {
                groupExpr = root.get("reportMonth");
                labelExpr = root.get("reportMonth");
                groupByList.add(groupExpr);
            }
            case YEAR -> {
                groupExpr = root.get("reportYear");
                labelExpr = root.get("reportYear");
                groupByList.add(groupExpr);
            }
            case STORE -> {
                groupExpr = storeJoin.get("storeId");
                labelExpr = storeJoin.get("storeName");
                groupByList.add(groupExpr);
                groupByList.add(labelExpr);
            }
            case DEPARTMENT -> {
                groupExpr = root.get("departmentId");
                labelExpr = root.get("departmentId");
                groupByList.add(groupExpr);
            }
            default -> throw new BadRequestException("Invalid groupBy for MONTHLY reports");
        }

        List<Predicate> predicates = buildMonthlyPredicates(cb, root, storeJoin, request);
        List<Selection<?>> selections = buildSelections(cb, root, labelExpr, request);

        q.multiselect(selections)
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(groupByList)
                .orderBy(cb.asc(groupExpr));

        List<Tuple> tuples = entityManager.createQuery(q).getResultList();
        return toResponse(tuples, request);
    }

    private AnalyticsResponse executeGasMonthlyAnalytics(AnalyticsRequest request) {
        Long storeId = request.getStoreIds().get(0);
        GasSalesReportMonthly reportA = findGasReport(
                storeId, request.getComparisonAMonth(), request.getComparisonAYear());
        GasSalesReportMonthly reportB = findGasReport(
                storeId, request.getComparisonBMonth(), request.getComparisonBYear());
        List<String> metrics = expandGasMetrics(request.getMetric(), reportA, reportB);

        List<DatasetDto> datasets = metrics.stream()
                .map(metric -> toGasDataset(metric, reportA, reportB))
                .toList();

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("reportType", request.getReportType());
        meta.put("groupBy", request.getGroupBy());
        meta.put("storeId", storeId);
        meta.put("storeName", reportA.getStore().getStoreName());
        meta.put("comparisonAMonth", request.getComparisonAMonth());
        meta.put("comparisonAYear", request.getComparisonAYear());
        meta.put("comparisonBMonth", request.getComparisonBMonth());
        meta.put("comparisonBYear", request.getComparisonBYear());
        meta.put("totalDataPoints", datasets.size());

        return AnalyticsResponse.builder()
                .labels(List.of("comparisonA", "comparisonB"))
                .datasets(datasets)
                .meta(meta)
                .build();
    }

    private List<String> expandGasMetrics(
            List<String> requestedMetrics,
            GasSalesReportMonthly reportA,
            GasSalesReportMonthly reportB) {
        boolean includeAllMetrics = requestedMetrics.stream()
                .anyMatch(metric -> "ALL".equalsIgnoreCase(metric));
        if (!includeAllMetrics) {
            return requestedMetrics;
        }

        Set<String> expandedMetrics = new LinkedHashSet<>();
        requestedMetrics.stream()
                .filter(metric -> !"ALL".equalsIgnoreCase(metric))
                .map(this::normalizeGasMetric)
                .forEach(expandedMetrics::add);
        expandedMetrics.addAll(GAS_MONTHLY_METRICS);

        Set<Long> fuelTypeIds = new TreeSet<>();
        collectFuelTypeIds(reportA, fuelTypeIds);
        collectFuelTypeIds(reportB, fuelTypeIds);
        for (Long fuelTypeId : fuelTypeIds) {
            expandedMetrics.add("DETAIL_VOLUME_SOLD_" + fuelTypeId);
            expandedMetrics.add("DETAIL_PROFIT_" + fuelTypeId);
        }
        return List.copyOf(expandedMetrics);
    }

    private void collectFuelTypeIds(GasSalesReportMonthly report, Set<Long> fuelTypeIds) {
        report.getDetails().stream()
                .map(detail -> detail.getFuelType())
                .filter(Objects::nonNull)
                .map(fuelType -> fuelType.getFuelTypeId())
                .filter(Objects::nonNull)
                .forEach(fuelTypeIds::add);
    }

    private GasSalesReportMonthly findGasReport(Long storeId, Integer month, Integer year) {
        return gasSalesReportMonthlyRepository
                .findByStoreStoreIdAndReportMonthAndReportYear(storeId, month, year)
                .orElseThrow(() -> new ResourceNotFoundException("Gas sales monthly report not found"));
    }

    private AnalyticsResponse executeLotteryMonthlyAnalytics(AnalyticsRequest request) {
        Long storeId = request.getStoreIds().get(0);
        LotterySalesReportMonthly reportA = findLotteryReport(
                storeId, request.getComparisonAMonth(), request.getComparisonAYear());
        LotterySalesReportMonthly reportB = findLotteryReport(
                storeId, request.getComparisonBMonth(), request.getComparisonBYear());
        List<String> metrics = expandLotteryMetrics(request.getMetric());

        List<DatasetDto> datasets = metrics.stream()
                .map(metric -> toLotteryDataset(metric, reportA, reportB))
                .toList();

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("reportType", request.getReportType());
        meta.put("groupBy", request.getGroupBy());
        meta.put("storeId", storeId);
        meta.put("storeName", reportA.getStore().getStoreName());
        meta.put("comparisonAMonth", request.getComparisonAMonth());
        meta.put("comparisonAYear", request.getComparisonAYear());
        meta.put("comparisonBMonth", request.getComparisonBMonth());
        meta.put("comparisonBYear", request.getComparisonBYear());
        meta.put("totalDataPoints", datasets.size());

        return AnalyticsResponse.builder()
                .labels(List.of("comparisonA", "comparisonB"))
                .datasets(datasets)
                .meta(meta)
                .build();
    }

    private LotterySalesReportMonthly findLotteryReport(Long storeId, Integer month, Integer year) {
        return lotterySalesReportMonthlyRepository
                .findByStoreIdAndReportMonthAndReportYear(storeId, month, year)
                .orElseThrow(() -> new ResourceNotFoundException("Lottery sales monthly report not found"));
    }

    private List<String> expandLotteryMetrics(List<String> requestedMetrics) {
        boolean includeAllMetrics = requestedMetrics.stream()
                .anyMatch(metric -> "ALL".equalsIgnoreCase(metric));
        if (!includeAllMetrics) {
            return requestedMetrics;
        }

        Set<String> expandedMetrics = new LinkedHashSet<>();
        requestedMetrics.stream()
                .filter(metric -> !"ALL".equalsIgnoreCase(metric))
                .map(this::normalizeLotteryMetric)
                .forEach(expandedMetrics::add);
        expandedMetrics.addAll(LOTTERY_MONTHLY_METRICS);
        return List.copyOf(expandedMetrics);
    }

    private DatasetDto toLotteryDataset(
            String metric,
            LotterySalesReportMonthly reportA,
            LotterySalesReportMonthly reportB) {
        String normalizedMetric = normalizeLotteryMetric(metric);
        BigDecimal valueA = lotteryMetricValue(normalizedMetric, reportA);
        BigDecimal valueB = lotteryMetricValue(normalizedMetric, reportB);
        BigDecimal difference = valueA.subtract(valueB);
        BigDecimal percentageDifference = valueB.signum() == 0
                ? BigDecimal.ZERO
                : difference.divide(valueB, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);

        return DatasetDto.builder()
                .label(METRIC_LABELS.get(normalizedMetric))
                .metric(normalizedMetric)
                .data(List.of(valueA, valueB))
                .valueA(valueA)
                .valueB(valueB)
                .difference(difference)
                .percentageDifference(percentageDifference)
                .build();
    }

    private String normalizeLotteryMetric(String metric) {
        if (metric == null) {
            return null;
        }
        return switch (metric) {
            case "onlineSales", "ONLINE_SALES" -> "ONLINE_SALES";
            case "scratchOffSales", "SCRATCH_OFF_SALES" -> "SCRATCH_OFF_SALES";
            case "onlineCashes", "ONLINE_CASHES" -> "ONLINE_CASHES";
            case "scratchOffCashes", "SCRATCH_OFF_CASHES" -> "SCRATCH_OFF_CASHES";
            case "commission", "COMMISSION" -> "COMMISSION";
            default -> metric.toUpperCase(Locale.ROOT);
        };
    }

    private BigDecimal lotteryMetricValue(String metric, LotterySalesReportMonthly report) {
        return switch (metric) {
            case "ONLINE_SALES" -> report.getOnlineSales();
            case "SCRATCH_OFF_SALES" -> report.getScratchOffSales();
            case "ONLINE_CASHES" -> report.getOnlineCashes();
            case "SCRATCH_OFF_CASHES" -> report.getScratchOffCashes();
            case "COMMISSION" -> report.getCommission();
            default -> throw new BadRequestException("Unsupported lottery metric: " + metric);
        };
    }

    private DatasetDto toGasDataset(String metric, GasSalesReportMonthly reportA,
            GasSalesReportMonthly reportB) {
        String normalizedMetric = normalizeGasMetric(metric);
        BigDecimal valueA = gasMetricValue(normalizedMetric, reportA);
        BigDecimal valueB = gasMetricValue(normalizedMetric, reportB);
        BigDecimal difference = valueA.subtract(valueB);
        BigDecimal percentageDifference = valueB.signum() == 0
                ? BigDecimal.ZERO
                : difference.divide(valueB, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);

        return DatasetDto.builder()
                .label(gasMetricLabel(normalizedMetric, reportA, reportB))
                .metric(normalizedMetric)
                .data(List.of(valueA, valueB))
                .valueA(valueA)
                .valueB(valueB)
                .difference(difference)
                .percentageDifference(percentageDifference)
                .build();
    }

    private String normalizeGasMetric(String metric) {
        if (metric == null) {
            return null;
        }
        return switch (metric) {
            case "creditFees", "CREDIT_FEES" -> "CREDIT_FEES";
            case "totalVolumeSold", "TOTAL_VOLUME_SOLD" -> "TOTAL_VOLUME_SOLD";
            case "netProfit", "NET_PROFIT" -> "NET_PROFIT";
            case "netProfitPerGallon", "NET_PROFIT_PER_GALLON" -> "NET_PROFIT_PER_GALLON";
            default -> metric.toUpperCase(Locale.ROOT);
        };
    }

    private BigDecimal gasMetricValue(String metric, GasSalesReportMonthly report) {
        GasDetailMetric detailMetric = parseGasDetailMetric(metric);
        if (detailMetric != null) {
            return report.getDetails().stream()
                    .filter(detail -> detail.getFuelType() != null
                            && Objects.equals(detailMetric.fuelTypeId(), detail.getFuelType().getFuelTypeId()))
                    .findFirst()
                    .map(detail -> detailMetric.type() == GasDetailMetricType.VOLUME_SOLD
                            ? detail.getVolumeSold()
                            : detail.getProfitPerGallon())
                    .orElse(BigDecimal.ZERO);
        }

        return switch (metric) {
            case "CREDIT_FEES" -> report.getCreditFees();
            case "TOTAL_VOLUME_SOLD" -> report.getTotalVolumeSold();
            case "NET_PROFIT" -> report.getNetProfit();
            case "NET_PROFIT_PER_GALLON" -> report.getNetProfitPerGallon();
            default -> throw new BadRequestException("Unsupported gas metric: " + metric);
        };
    }

    private String gasMetricLabel(
            String metric,
            GasSalesReportMonthly reportA,
            GasSalesReportMonthly reportB) {
        GasDetailMetric detailMetric = parseGasDetailMetric(metric);
        if (detailMetric == null) {
            return METRIC_LABELS.get(metric);
        }
        String metricName = detailMetric.type() == GasDetailMetricType.VOLUME_SOLD
                ? "Volume Sold"
                : "Profit Per Gallon";
        String fuelName = findFuelName(reportA, detailMetric.fuelTypeId())
                .or(() -> findFuelName(reportB, detailMetric.fuelTypeId()))
                .orElse("Unknown Fuel");
        return metricName + " (" + fuelName + ")";
    }

    private Optional<String> findFuelName(GasSalesReportMonthly report, Long fuelTypeId) {
        return report.getDetails().stream()
                .filter(detail -> detail.getFuelType() != null
                        && Objects.equals(fuelTypeId, detail.getFuelType().getFuelTypeId()))
                .map(detail -> detail.getFuelType().getFuelName())
                .filter(Objects::nonNull)
                .findFirst();
    }

    private GasDetailMetric parseGasDetailMetric(String metric) {
        if (metric == null) {
            return null;
        }
        Matcher matcher = GAS_DETAIL_METRIC_PATTERN.matcher(metric);
        if (!matcher.matches()) {
            return null;
        }
        try {
            return new GasDetailMetric(
                    GasDetailMetricType.valueOf(matcher.group(1)),
                    Long.parseLong(matcher.group(2)));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private enum GasDetailMetricType {
        VOLUME_SOLD,
        PROFIT
    }

    private record GasDetailMetric(GasDetailMetricType type, Long fuelTypeId) {
    }

    private AnalyticsResponse executeMultiYearMonthAnalytics(AnalyticsRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<MonthlyReport> root = q.from(MonthlyReport.class);
        Join<MonthlyReport, Store> storeJoin = root.join("store", JoinType.LEFT);

        Expression<Integer> yearExpr = root.get("reportYear");
        Expression<Integer> monthExpr = root.get("reportMonth");

        List<Selection<?>> selections = new ArrayList<>();
        selections.add(yearExpr.alias("labelYear"));
        selections.add(monthExpr.alias("labelMonth"));
        for (String metric : request.getMetric()) {
            Expression<BigDecimal> field = root.get(metric);
            selections.add(applyAggregate(cb, field, request.getAggregate()).alias(metric));
        }

        List<Predicate> predicates = buildMonthlyPredicates(cb, root, storeJoin, request);

        q.multiselect(selections)
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(yearExpr, monthExpr)
                .orderBy(cb.asc(yearExpr), cb.asc(monthExpr));

        List<Tuple> tuples = entityManager.createQuery(q).getResultList();
        return toResponse(tuples, request, t -> String.format("%d-%02d",
                t.get("labelYear", Integer.class), t.get("labelMonth", Integer.class)));
    }

    private AnalyticsResponse executeQuarterAnalytics(AnalyticsRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createTupleQuery();
        Root<MonthlyReport> root = q.from(MonthlyReport.class);
        Join<MonthlyReport, Store> storeJoin = root.join("store", JoinType.LEFT);

        // Quarter derived in SQL: FLOOR((report_month - 1) / 3) + 1
        Expression<Integer> monthExpr = root.get("reportMonth");
        Expression<Integer> quarterExpr = cb.sum(
                cb.quot(cb.diff(monthExpr, 1), 3).as(Integer.class), 1);

        boolean multiYear = request.getYear().size() > 1;
        Expression<Integer> yearExpr = root.get("reportYear");

        List<Selection<?>> selections = new ArrayList<>();
        List<Expression<?>> groupByList = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        if (multiYear) {
            selections.add(yearExpr.alias("labelYear"));
            groupByList.add(yearExpr);
            orders.add(cb.asc(yearExpr));
        }
        selections.add(quarterExpr.alias("labelQuarter"));
        groupByList.add(quarterExpr);
        orders.add(cb.asc(quarterExpr));

        for (String metric : request.getMetric()) {
            Expression<BigDecimal> field = root.get(metric);
            selections.add(applyAggregate(cb, field, request.getAggregate()).alias(metric));
        }

        List<Predicate> predicates = buildMonthlyPredicates(cb, root, storeJoin, request);

        q.multiselect(selections)
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(groupByList)
                .orderBy(orders);

        List<Tuple> tuples = entityManager.createQuery(q).getResultList();
        return toResponse(tuples, request, multiYear
                ? t -> t.get("labelYear", Integer.class) + "-Q" + ((Number) t.get("labelQuarter")).intValue()
                : t -> "Q" + ((Number) t.get("labelQuarter")).intValue());
    }

    private List<Predicate> buildMonthlyPredicates(CriteriaBuilder cb, Root<MonthlyReport> root,
            Join<MonthlyReport, Store> storeJoin,
            AnalyticsRequest request) {
        List<Predicate> predicates = new ArrayList<>();
        if (!isNullOrEmpty(request.getStoreIds())) {
            predicates.add(storeJoin.get("storeId").in(request.getStoreIds()));
        }
        if (!isNullOrEmpty(request.getYear())) {
            predicates.add(root.get("reportYear").in(request.getYear()));
        }
        if (request.getMonth() != null) {
            predicates.add(cb.equal(root.get("reportMonth"), request.getMonth()));
        }
        if (request.getDepartmentId() != null) {
            predicates.add(cb.equal(root.get("departmentId"), request.getDepartmentId()));
        }
        return predicates;
    }

    // ─── Shared Helpers ───────────────────────────────────────────────────────

    private <T> List<Selection<?>> buildSelections(CriteriaBuilder cb, Root<T> root,
            Expression<?> labelExpr,
            AnalyticsRequest request) {
        List<Selection<?>> selections = new ArrayList<>();
        selections.add(labelExpr.alias("label"));
        for (String metric : request.getMetric()) {
            Expression<BigDecimal> field = root.<BigDecimal>get(metric);
            selections.add(applyAggregate(cb, field, request.getAggregate()).alias(metric));
        }
        return selections;
    }

    private Expression<?> applyAggregate(CriteriaBuilder cb, Expression<BigDecimal> field,
            AggregateType aggregate) {
        return switch (aggregate) {
            case SUM -> cb.sum(field);
            case AVG -> cb.avg(field);
            case MAX -> cb.max(field);
            case MIN -> cb.min(field);
        };
    }

    private AnalyticsResponse toResponse(List<Tuple> tuples, AnalyticsRequest request) {
        return toResponse(tuples, request, t -> String.valueOf(t.get("label")));
    }

    private AnalyticsResponse toResponse(List<Tuple> tuples, AnalyticsRequest request,
            java.util.function.Function<Tuple, String> labelMapper) {
        List<String> labels = tuples.stream()
                .map(labelMapper)
                .toList();

        List<DatasetDto> datasets = request.getMetric().stream()
                .map(metric -> DatasetDto.builder()
                        .label(METRIC_LABELS.getOrDefault(metric, metric))
                        .metric(metric)
                        .data(tuples.stream().map(t -> t.get(metric)).toList())
                        .build())
                .toList();

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("reportType", request.getReportType());
        meta.put("groupBy", request.getGroupBy());
        meta.put("aggregate", request.getAggregate());
        if (!isNullOrEmpty(request.getStoreIds()))
            meta.put("storeIds", request.getStoreIds());
        if (!isNullOrEmpty(request.getYear()))
            meta.put("year", request.getYear());
        if (request.getMonth() != null)
            meta.put("month", request.getMonth());
        if (request.getFrom() != null)
            meta.put("from", request.getFrom().toString());
        if (request.getTo() != null)
            meta.put("to", request.getTo().toString());
        meta.put("totalDataPoints", labels.size());

        return AnalyticsResponse.builder()
                .labels(labels)
                .datasets(datasets)
                .meta(meta)
                .build();
    }

    private boolean isNullOrEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
