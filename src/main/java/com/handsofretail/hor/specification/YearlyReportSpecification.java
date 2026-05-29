package com.handsofretail.hor.specification;

import com.handsofretail.hor.entity.YearlyReport;
import org.springframework.data.jpa.domain.Specification;

public final class YearlyReportSpecification {

    private YearlyReportSpecification() {
    }

    public static Specification<YearlyReport> hasStoreId(Long storeId) {
        return (root, query, cb) -> cb.equal(root.get("store").get("storeId"), storeId);
    }

    public static Specification<YearlyReport> hasClientId(Long clientId) {
        return (root, query, cb) -> cb.equal(root.get("store").get("client").get("clientId"), clientId);
    }

    public static Specification<YearlyReport> hasYear(Integer year) {
        return (root, query, cb) -> cb.equal(root.get("reportYear"), year);
    }
}