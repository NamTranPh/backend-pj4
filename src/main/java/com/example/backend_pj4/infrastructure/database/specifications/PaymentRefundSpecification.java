package com.example.backend_pj4.infrastructure.database.specifications;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.backend_pj4.domain.repository.filter.PaymentRefundFilter;
import com.example.backend_pj4.infrastructure.database.entities.PaymentRefundJpaEntity;

import jakarta.persistence.criteria.Predicate;

public final class PaymentRefundSpecification {

    private PaymentRefundSpecification() {
    }

    public static Specification<PaymentRefundJpaEntity> buildFilter(PaymentRefundFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) {
                return cb.conjunction();
            }

            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (StringUtils.hasText(filter.paymentOrderId())) {
                predicates.add(cb.equal(root.get("paymentOrder").get("id"), filter.paymentOrderId()));
            }

            if (StringUtils.hasText(filter.userId())) {
                predicates.add(cb.equal(root.get("user").get("id"), filter.userId()));
            }

            if (filter.fromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("requestedAt"),
                        filter.fromDate().atStartOfDay()
                ));
            }

            if (filter.toDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("requestedAt"),
                        filter.toDate().atTime(LocalTime.MAX)
                ));
            }

            if (StringUtils.hasText(filter.search())) {
                String pattern = "%" + filter.search().toLowerCase().trim() + "%";
                Predicate reason = cb.like(cb.lower(root.get("reason")), pattern);
                Predicate code = cb.like(cb.lower(root.get("refundTransactionCode")), pattern);
                Predicate userEmail = cb.like(cb.lower(root.get("user").get("email")), pattern);
                predicates.add(cb.or(reason, code, userEmail));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
