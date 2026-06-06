package com.handsofretail.hor.specification;

import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.enums.Status;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class StoreSpecification {

    private StoreSpecification() {
    }

    public static Specification<Store> hasClientId(Long clientId) {
        return (root, query, cb) -> {
            query.distinct(true);
            var mappings = root.join("mappings", JoinType.INNER);
            return cb.equal(mappings.get("id").get("clientId"), clientId);
        };
    }

    public static Specification<Store> hasStatus(Status status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}
