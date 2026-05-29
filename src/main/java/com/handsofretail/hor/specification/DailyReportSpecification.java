package com.handsofretail.hor.specification;

import com.handsofretail.hor.entity.DailyReport;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class DailyReportSpecification {

    private DailyReportSpecification() {
    }

    public static Specification<DailyReport> hasStoreId(Long storeId) {
        return (root, query, cb) -> cb.equal(root.get("store").get("storeId"), storeId);
    }

    public static Specification<DailyReport> hasClientId(Long clientId) {
        return (root, query, cb) -> cb.equal(root.get("store").get("client").get("clientId"), clientId);
    }

    public static Specification<DailyReport> reportDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> cb.between(root.get("reportDate"), from, to);
    }

    public static Specification<DailyReport> reportDateFrom(LocalDate from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("reportDate"), from);
    }

    public static Specification<DailyReport> reportDateTo(LocalDate to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("reportDate"), to);
    }
}