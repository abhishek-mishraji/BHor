package com.handsofretail.hor.specification;

import com.handsofretail.hor.entity.Store;
import com.handsofretail.hor.enums.Status;
import org.springframework.data.jpa.domain.Specification;

public final class StoreSpecification {

    private StoreSpecification() {
    }

    public static Specification<Store> hasClientId(Long clientId) {
        return (root, query, cb) -> cb.equal(root.get("client").get("clientId"), clientId);
    }

    public static Specification<Store> hasStatus(Status status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}