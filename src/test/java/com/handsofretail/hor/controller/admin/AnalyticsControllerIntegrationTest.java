package com.handsofretail.hor.controller.admin;

import com.handsofretail.hor.entity.ClientStoreId;
import com.handsofretail.hor.entity.ClientStoreMapping;
import com.handsofretail.hor.entity.ClientUser;
import com.handsofretail.hor.entity.MonthlyReport;
import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.enums.Status;
import com.handsofretail.hor.enums.StoreRole;
import com.handsofretail.hor.enums.UserRole;
import com.handsofretail.hor.repository.ClientStoreMappingRepository;
import com.handsofretail.hor.repository.ClientUserRepository;
import com.handsofretail.hor.repository.MonthlyReportRepository;
import com.handsofretail.hor.repository.StoreRepository;
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

    private Store store;

    @BeforeEach
    void setUp() {
        monthlyReportRepository.deleteAll();
        clientStoreMappingRepository.deleteAll();
        storeRepository.deleteAll();
        clientUserRepository.deleteAll();

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
}
