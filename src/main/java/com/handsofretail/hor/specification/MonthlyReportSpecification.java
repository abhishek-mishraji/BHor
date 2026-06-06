package com.handsofretail.hor.specification;

import com.handsofretail.hor.entity.MonthlyReport;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class MonthlyReportSpecification {

    private MonthlyReportSpecification() {
    }

    public static Specification<MonthlyReport> hasStoreId(Long storeId) {
        return (root, query, cb) -> cb.equal(root.get("store").get("storeId"), storeId);
    }

    public static Specification<MonthlyReport> hasClientId(Long clientId) {
        return (root, query, cb) -> {
            query.distinct(true);
            var store = root.join("store", JoinType.INNER);
            var mappings = store.join("mappings", JoinType.INNER);
            return cb.equal(mappings.get("id").get("clientId"), clientId);
        };
    }

    public static Specification<MonthlyReport> hasYear(Integer year) {
        return (root, query, cb) -> cb.equal(root.get("reportYear"), year);
    }

    public static Specification<MonthlyReport> hasMonth(Integer month) {
        return (root, query, cb) -> cb.equal(root.get("reportMonth"), month);
    }
}
