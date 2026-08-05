package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.entity.ClientStoreId;
import com.handsofretail.hor.entity.ClientStoreMapping;
import com.handsofretail.hor.entity.ClientUser;
import com.handsofretail.hor.entity.FuelType;
import com.handsofretail.hor.entity.GasSalesReportDetail;
import com.handsofretail.hor.entity.GasSalesReportMonthly;
import com.handsofretail.hor.entity.LotterySalesReportMonthly;
import com.handsofretail.hor.entity.MonthlyReport;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.enums.Status;
import com.handsofretail.hor.enums.StoreRole;
import com.handsofretail.hor.enums.UserRole;
import com.handsofretail.hor.repository.ClientStoreMappingRepository;
import com.handsofretail.hor.repository.ClientUserRepository;
import com.handsofretail.hor.repository.FuelTypeRepository;
import com.handsofretail.hor.repository.GasSalesReportMonthlyRepository;
import com.handsofretail.hor.repository.LotterySalesReportMonthlyRepository;
import com.handsofretail.hor.repository.MonthlyReportRepository;
import com.handsofretail.hor.repository.StoreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnalyticsControllerIntegrationTest {

    private static final String ADMIN_URL = "/api/v1/admin/analytics/reports";
    private static final String CLIENT_URL = "/api/v1/client/analytics/reports";

    @Autowired private MockMvc mockMvc;
    @Autowired private StoreRepository storeRepository;
    @Autowired private MonthlyReportRepository monthlyReportRepository;
    @Autowired private ClientUserRepository clientUserRepository;
    @Autowired private ClientStoreMappingRepository clientStoreMappingRepository;
    @Autowired private GasSalesReportMonthlyRepository gasSalesReportMonthlyRepository;
    @Autowired private LotterySalesReportMonthlyRepository lotterySalesReportMonthlyRepository;
    @Autowired private FuelTypeRepository fuelTypeRepository;

    private Store store;

    @BeforeEach
    void setUp() {
        lotterySalesReportMonthlyRepository.deleteAll();
        gasSalesReportMonthlyRepository.deleteAll();
        monthlyReportRepository.deleteAll();
        clientStoreMappingRepository.deleteAll();
        storeRepository.deleteAll();
        clientUserRepository.deleteAll();
        fuelTypeRepository.deleteAll();

        store = storeRepository.save(Store.builder()
                .storeName("Analytics Store")
                .storeCode("ANL-01")
                .status(Status.ACTIVE)
                .build());

        // 2025: Jan, Feb, Mar — 2026: Jan, Feb. No other months (no zero-fill expected).
        saveReport(2025, 1, "41500.00", "1000.00");
        saveReport(2025, 2, "53200.00", "1100.00");
        saveReport(2025, 3, "48900.00", "1200.00");
        saveReport(2026, 1, "60100.00", "1300.00");
        saveReport(2026, 2, "55400.00", "1400.00");
    }

    @AfterEach
    void tearDown() {
        lotterySalesReportMonthlyRepository.deleteAll();
        gasSalesReportMonthlyRepository.deleteAll();
        monthlyReportRepository.deleteAll();
        clientStoreMappingRepository.deleteAll();
        storeRepository.deleteAll();
        clientUserRepository.deleteAll();
        fuelTypeRepository.deleteAll();
    }

    // -------------------------------------------------------------------------
    // Regression — single year keeps the legacy label shape ("1".."12")
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void monthlyGroupByMonth_singleYear_keepsLegacyMonthNumberLabels() throws Exception {
        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "netSales")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("year", "2025")
                        .param("aggregate", "SUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.labels.length()").value(3))
                .andExpect(jsonPath("$.data.labels[0]").value("1"))
                .andExpect(jsonPath("$.data.labels[1]").value("2"))
                .andExpect(jsonPath("$.data.labels[2]").value("3"))
                .andExpect(jsonPath("$.data.datasets[0].metric").value("netSales"))
                .andExpect(jsonPath("$.data.datasets[0].data[0]").value(41500.00))
                .andExpect(jsonPath("$.data.meta.year[0]").value(2025))
                .andExpect(jsonPath("$.data.meta.totalDataPoints").value(3));
    }

    // -------------------------------------------------------------------------
    // Multi-year — labels become "YYYY-MM", zero-padded, chronological
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void monthlyGroupByMonth_multiYear_returnsYearQualifiedLabels() throws Exception {
        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "netSales")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("year", "2025")
                        .param("year", "2026")
                        .param("aggregate", "SUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.labels.length()").value(5))
                .andExpect(jsonPath("$.data.labels[0]").value("2025-01"))
                .andExpect(jsonPath("$.data.labels[1]").value("2025-02"))
                .andExpect(jsonPath("$.data.labels[2]").value("2025-03"))
                .andExpect(jsonPath("$.data.labels[3]").value("2026-01"))
                .andExpect(jsonPath("$.data.labels[4]").value("2026-02"))
                .andExpect(jsonPath("$.data.datasets[0].data[0]").value(41500.00))
                .andExpect(jsonPath("$.data.datasets[0].data[3]").value(60100.00))
                .andExpect(jsonPath("$.data.meta.year[0]").value(2025))
                .andExpect(jsonPath("$.data.meta.year[1]").value(2026))
                .andExpect(jsonPath("$.data.meta.totalDataPoints").value(5));
    }

    // -------------------------------------------------------------------------
    // Multi-year + month filter — one bucket per year for that month
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void monthlyGroupByMonth_multiYearWithMonthFilter_returnsOneBucketPerYear() throws Exception {
        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "netSales")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("year", "2025")
                        .param("year", "2026")
                        .param("month", "1")
                        .param("aggregate", "SUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.labels.length()").value(2))
                .andExpect(jsonPath("$.data.labels[0]").value("2025-01"))
                .andExpect(jsonPath("$.data.labels[1]").value("2026-01"))
                .andExpect(jsonPath("$.data.datasets[0].data[0]").value(41500.00))
                .andExpect(jsonPath("$.data.datasets[0].data[1]").value(60100.00));
    }

    // -------------------------------------------------------------------------
    // Multi-year + multiple metrics — every dataset aligned with labels[]
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void monthlyGroupByMonth_multiYearMultiMetric_alignsAllDatasets() throws Exception {
        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "netSales")
                        .param("metric", "gross")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("year", "2025")
                        .param("year", "2026")
                        .param("aggregate", "SUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.labels.length()").value(5))
                .andExpect(jsonPath("$.data.datasets.length()").value(2))
                .andExpect(jsonPath("$.data.datasets[0].metric").value("netSales"))
                .andExpect(jsonPath("$.data.datasets[0].data.length()").value(5))
                .andExpect(jsonPath("$.data.datasets[1].metric").value("gross"))
                .andExpect(jsonPath("$.data.datasets[1].data.length()").value(5))
                .andExpect(jsonPath("$.data.datasets[1].data[4]").value(1400.00));
    }

    // -------------------------------------------------------------------------
    // Years with no data produce no buckets (no zero-fill, no nulls)
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void monthlyGroupByMonth_multiYear_emptyYearProducesNoBuckets() throws Exception {
        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "netSales")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("year", "2025")
                        .param("year", "2030")
                        .param("aggregate", "SUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.labels.length()").value(3))
                .andExpect(jsonPath("$.data.labels[0]").value("2025-01"))
                .andExpect(jsonPath("$.data.labels[2]").value("2025-03"))
                .andExpect(jsonPath("$.data.datasets[0].data.length()").value(3));
    }

    // -------------------------------------------------------------------------
    // Validation — groupBy=MONTH with no year still 400
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void monthlyGroupByMonth_withoutYear_returns400() throws Exception {
        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "netSales")
                        .param("storeIds", String.valueOf(store.getStoreId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("year is required when groupBy=MONTH"));
    }

    // -------------------------------------------------------------------------
    // Other groupBy untouched — YEAR grouping still returns year labels
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void monthlyGroupByYear_multiYear_behaviorUnchanged() throws Exception {
        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "MONTHLY")
                        .param("groupBy", "YEAR")
                        .param("metric", "netSales")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("year", "2025")
                        .param("year", "2026")
                        .param("aggregate", "SUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.labels[0]").value("2025"))
                .andExpect(jsonPath("$.data.labels[1]").value("2026"))
                .andExpect(jsonPath("$.data.datasets[0].data[0]").value(143600.00))
                .andExpect(jsonPath("$.data.datasets[0].data[1]").value(115500.00));
    }

    // -------------------------------------------------------------------------
    // Client endpoint — multi-year works with JWT-resolved stores
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(username = "client@example.com", roles = "CLIENT")
    void clientEndpoint_multiYearGroupByMonth_worksWithResolvedStores() throws Exception {
        ClientUser client = clientUserRepository.save(ClientUser.builder()
                .fullName("Client User")
                .email("client@example.com")
                .passwordHash("hash")
                .status(Status.ACTIVE)
                .role(UserRole.CLIENT)
                .build());

        clientStoreMappingRepository.save(ClientStoreMapping.builder()
                .id(new ClientStoreId(client.getClientId(), store.getStoreId()))
                .client(client)
                .store(store)
                .role(StoreRole.OWNER)
                .build());

        mockMvc.perform(get(CLIENT_URL)
                        .param("reportType", "MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "netSales")
                        .param("year", "2025")
                        .param("year", "2026")
                        .param("aggregate", "SUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.labels.length()").value(5))
                .andExpect(jsonPath("$.data.labels[0]").value("2025-01"))
                .andExpect(jsonPath("$.data.labels[4]").value("2026-02"))
                .andExpect(jsonPath("$.data.datasets[0].data[0]").value(41500.00));
    }

    // -------------------------------------------------------------------------
    // QUARTER — single year uses "Q1".."Q4" labels
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void monthlyGroupByQuarter_singleYear_returnsQuarterLabels() throws Exception {
        saveReport(2025, 4, "10000.00", "500.00"); // adds a Q2 bucket

        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "MONTHLY")
                        .param("groupBy", "QUARTER")
                        .param("metric", "netSales")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("year", "2025")
                        .param("aggregate", "SUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.labels.length()").value(2))
                .andExpect(jsonPath("$.data.labels[0]").value("Q1"))
                .andExpect(jsonPath("$.data.labels[1]").value("Q2"))
                // Q1 2025 = 41500 + 53200 + 48900
                .andExpect(jsonPath("$.data.datasets[0].data[0]").value(143600.00))
                .andExpect(jsonPath("$.data.datasets[0].data[1]").value(10000.00));
    }

    // -------------------------------------------------------------------------
    // QUARTER — multiple years use "YYYY-Qn" labels, chronological
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void monthlyGroupByQuarter_multiYear_returnsYearQualifiedQuarterLabels() throws Exception {
        saveReport(2026, 12, "20000.00", "900.00"); // adds a 2026-Q4 bucket

        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "MONTHLY")
                        .param("groupBy", "QUARTER")
                        .param("metric", "netSales")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("year", "2025")
                        .param("year", "2026")
                        .param("aggregate", "SUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.labels.length()").value(3))
                .andExpect(jsonPath("$.data.labels[0]").value("2025-Q1"))
                .andExpect(jsonPath("$.data.labels[1]").value("2026-Q1"))
                .andExpect(jsonPath("$.data.labels[2]").value("2026-Q4"))
                .andExpect(jsonPath("$.data.datasets[0].data[0]").value(143600.00))
                .andExpect(jsonPath("$.data.datasets[0].data[1]").value(115500.00))
                .andExpect(jsonPath("$.data.datasets[0].data[2]").value(20000.00));
    }

    // -------------------------------------------------------------------------
    // QUARTER — validation
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void groupByQuarter_withDailyReportType_returns400() throws Exception {
        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "DAILY")
                        .param("groupBy", "QUARTER")
                        .param("metric", "groceryTotal")
                        .param("storeIds", String.valueOf(store.getStoreId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void groupByQuarter_withoutYear_returns400() throws Exception {
        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "MONTHLY")
                        .param("groupBy", "QUARTER")
                        .param("metric", "netSales")
                        .param("storeIds", String.valueOf(store.getStoreId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("year is required when groupBy=QUARTER"));
    }

    // -------------------------------------------------------------------------
    // GAS_MONTHLY detail metrics
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void gasMonthlyDetailMetrics_returnRequestedFuelValuesAndZeroWhenMissing() throws Exception {
        FuelType regular = fuelTypeRepository.save(FuelType.builder().fuelName("Regular").build());
        FuelType diesel = fuelTypeRepository.save(FuelType.builder().fuelName("Diesel").build());
        saveGasReport(1, 2026, regular, "100.00", "0.25", diesel, "50.00", "0.40");
        saveGasReport(2, 2026, regular, "120.00", "0.30", null, null, null);

        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "GAS_MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "DETAIL_VOLUME_SOLD_" + regular.getFuelTypeId())
                        .param("metric", "DETAIL_PROFIT_" + regular.getFuelTypeId())
                        .param("metric", "DETAIL_VOLUME_SOLD_" + diesel.getFuelTypeId())
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("comparisonAMonth", "1")
                        .param("comparisonAYear", "2026")
                        .param("comparisonBMonth", "2")
                        .param("comparisonBYear", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.datasets[0].label")
                        .value("Volume Sold (Regular)"))
                .andExpect(jsonPath("$.data.datasets[0].data[0]").value(100.00))
                .andExpect(jsonPath("$.data.datasets[0].data[1]").value(120.00))
                .andExpect(jsonPath("$.data.datasets[1].label")
                        .value("Profit Per Gallon (Regular)"))
                .andExpect(jsonPath("$.data.datasets[1].data[0]").value(0.25))
                .andExpect(jsonPath("$.data.datasets[1].data[1]").value(0.30))
                .andExpect(jsonPath("$.data.datasets[2].data[0]").value(50.00))
                .andExpect(jsonPath("$.data.datasets[2].data[1]").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void gasMonthlyDetailMetric_usesFuelNameFromComparisonBWhenAbsentFromComparisonA() throws Exception {
        FuelType regular = fuelTypeRepository.save(FuelType.builder().fuelName("Regular").build());
        FuelType diesel = fuelTypeRepository.save(FuelType.builder().fuelName("Diesel").build());
        saveGasReport(1, 2026, regular, "100.00", "0.25", null, null, null);
        saveGasReport(2, 2026, diesel, "50.00", "0.40", null, null, null);

        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "GAS_MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "DETAIL_PROFIT_" + diesel.getFuelTypeId())
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("comparisonAMonth", "1")
                        .param("comparisonAYear", "2026")
                        .param("comparisonBMonth", "2")
                        .param("comparisonBYear", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.datasets[0].label").value("Profit Per Gallon (Diesel)"))
                .andExpect(jsonPath("$.data.datasets[0].data[0]").value(0))
                .andExpect(jsonPath("$.data.datasets[0].data[1]").value(0.40));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void gasMonthlyDetailMetric_withInvalidFormat_returns400() throws Exception {
        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "GAS_MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "DETAIL_VOLUME_SOLD_invalid")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("comparisonAMonth", "1")
                        .param("comparisonAYear", "2026")
                        .param("comparisonBMonth", "2")
                        .param("comparisonBYear", "2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void gasMonthlyAllMetric_expandsToRootAndAllUniqueFuelDetailMetrics() throws Exception {
        FuelType regular = fuelTypeRepository.save(FuelType.builder().fuelName("Regular").build());
        FuelType diesel = fuelTypeRepository.save(FuelType.builder().fuelName("Diesel").build());
        saveGasReport(1, 2026, regular, "100.00", "0.25", diesel, "50.00", "0.40");
        saveGasReport(2, 2026, regular, "120.00", "0.30", null, null, null);

        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "GAS_MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "all")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("comparisonAMonth", "1")
                        .param("comparisonAYear", "2026")
                        .param("comparisonBMonth", "2")
                        .param("comparisonBYear", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.datasets.length()").value(8))
                .andExpect(jsonPath("$.data.datasets[0].metric").value("CREDIT_FEES"))
                .andExpect(jsonPath("$.data.datasets[3].metric").value("NET_PROFIT_PER_GALLON"))
                .andExpect(jsonPath("$.data.datasets[4].metric")
                        .value("DETAIL_VOLUME_SOLD_" + regular.getFuelTypeId()))
                .andExpect(jsonPath("$.data.datasets[7].metric")
                        .value("DETAIL_PROFIT_" + diesel.getFuelTypeId()))
                .andExpect(jsonPath("$.data.datasets[6].data[1]").value(0));
    }

    // -------------------------------------------------------------------------
    // LOTTERY_MONTHLY comparison metrics
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void lotteryMonthlyAllMetric_returnsEveryMetricWithComparisonValues() throws Exception {
        saveLotteryReport(1, 2026, "150.00", "80.00", "20.00", "10.00", "30.00");
        saveLotteryReport(2, 2026, "100.00", "60.00", "10.00", "20.00", "25.00");

        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "LOTTERY_MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "ALL")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("comparisonAMonth", "1")
                        .param("comparisonAYear", "2026")
                        .param("comparisonBMonth", "2")
                        .param("comparisonBYear", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.labels[0]").value("comparisonA"))
                .andExpect(jsonPath("$.data.datasets.length()").value(5))
                .andExpect(jsonPath("$.data.datasets[0].metric").value("ONLINE_SALES"))
                .andExpect(jsonPath("$.data.datasets[0].label").value("Online Sales"))
                .andExpect(jsonPath("$.data.datasets[0].valueA").value(150.00))
                .andExpect(jsonPath("$.data.datasets[0].valueB").value(100.00))
                .andExpect(jsonPath("$.data.datasets[0].difference").value(50.00))
                .andExpect(jsonPath("$.data.datasets[0].percentageDifference").value(50.00))
                .andExpect(jsonPath("$.data.datasets[4].metric").value("COMMISSION"))
                .andExpect(jsonPath("$.data.datasets[4].data[1]").value(25.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void lotteryMonthlyMetric_withInvalidKey_returns400() throws Exception {
        mockMvc.perform(get(ADMIN_URL)
                        .param("reportType", "LOTTERY_MONTHLY")
                        .param("groupBy", "MONTH")
                        .param("metric", "INVALID")
                        .param("storeIds", String.valueOf(store.getStoreId()))
                        .param("comparisonAMonth", "1")
                        .param("comparisonAYear", "2026")
                        .param("comparisonBMonth", "2")
                        .param("comparisonBYear", "2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void saveReport(int year, int month, String netSales, String gross) {
        monthlyReportRepository.save(MonthlyReport.builder()
                .store(store)
                .reportYear(year)
                .reportMonth(month)
                .netSales(new BigDecimal(netSales))
                .gross(new BigDecimal(gross))
                .discount(BigDecimal.ZERO)
                .promotion(BigDecimal.ZERO)
                .refund(BigDecimal.ZERO)
                .voidAmount(BigDecimal.ZERO)
                .build());
    }

    private void saveGasReport(
            int month,
            int year,
            FuelType firstFuelType,
            String firstVolumeSold,
            String firstProfitPerGallon,
            FuelType secondFuelType,
            String secondVolumeSold,
            String secondProfitPerGallon) {
        GasSalesReportMonthly report = GasSalesReportMonthly.builder()
                .store(store)
                .reportMonth(month)
                .reportYear(year)
                .creditFees(BigDecimal.ZERO)
                .totalVolumeSold(BigDecimal.ZERO)
                .netProfitPerGallon(BigDecimal.ZERO)
                .netProfit(BigDecimal.ZERO)
                .build();
        addGasDetail(report, firstFuelType, firstVolumeSold, firstProfitPerGallon);
        if (secondFuelType != null) {
            addGasDetail(report, secondFuelType, secondVolumeSold, secondProfitPerGallon);
        }
        gasSalesReportMonthlyRepository.save(report);
    }

    private void addGasDetail(
            GasSalesReportMonthly report,
            FuelType fuelType,
            String volumeSold,
            String profitPerGallon) {
        report.getDetails().add(GasSalesReportDetail.builder()
                .gasSalesReportMonthly(report)
                .fuelType(fuelType)
                .volumeSold(new BigDecimal(volumeSold))
                .profitPerGallon(new BigDecimal(profitPerGallon))
                .build());
    }

    private void saveLotteryReport(
            int month,
            int year,
            String onlineSales,
            String scratchOffSales,
            String onlineCashes,
            String scratchOffCashes,
            String commission) {
        lotterySalesReportMonthlyRepository.save(LotterySalesReportMonthly.builder()
                .store(store)
                .reportMonth(month)
                .reportYear(year)
                .onlineSales(new BigDecimal(onlineSales))
                .scratchOffSales(new BigDecimal(scratchOffSales))
                .onlineCashes(new BigDecimal(onlineCashes))
                .scratchOffCashes(new BigDecimal(scratchOffCashes))
                .commission(new BigDecimal(commission))
                .build());
    }
}
