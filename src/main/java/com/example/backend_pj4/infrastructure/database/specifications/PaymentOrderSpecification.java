package com.example.backend_pj4.infrastructure.database.specifications;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.backend_pj4.domain.repository.filter.PaymentOrderFilter;
import com.example.backend_pj4.infrastructure.database.entities.PaymentOrderJpaEntity;

import jakarta.persistence.criteria.Predicate;

public final class PaymentOrderSpecification {

    private PaymentOrderSpecification() {
    }

    public static Specification<PaymentOrderJpaEntity> buildFilter(PaymentOrderFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) {
                return cb.conjunction();
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (StringUtils.hasText(filter.gateway())) {
                predicates.add(cb.equal(cb.upper(root.get("gateway")), filter.gateway().toUpperCase()));
            }

            if (StringUtils.hasText(filter.membershipPlanId())) {
                predicates.add(cb.equal(root.get("plan").get("id"), filter.membershipPlanId()));
            }

            if (StringUtils.hasText(filter.userId())) {
                predicates.add(cb.equal(root.get("user").get("id"), filter.userId()));
            }

            if (filter.fromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        filter.fromDate().atStartOfDay()
                ));
            }

            if (filter.toDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("createdAt"),
                        filter.toDate().atTime(LocalTime.MAX)
                ));
            }

            if (StringUtils.hasText(filter.search())) {
                String searchPattern = "%" + filter.search().toLowerCase().trim() + "%";
                Predicate txCode = cb.like(cb.lower(root.get("transactionCode")), searchPattern);
                Predicate gatewayTxId = cb.like(cb.lower(root.get("gatewayTransactionId")), searchPattern);
                Predicate userEmail = cb.like(cb.lower(root.get("user").get("email")), searchPattern);
                Predicate userName = cb.like(cb.lower(root.get("user").get("name")), searchPattern);
                predicates.add(cb.or(txCode, gatewayTxId, userEmail, userName));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
