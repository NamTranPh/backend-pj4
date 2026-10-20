/** Bộ lọc động cho đánh giá: theo phim, theo người, hoặc cả hai. */
package com.example.backend_pj4.infrastructure.database.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.backend_pj4.domain.repository.filter.RatingFilter;
import com.example.backend_pj4.infrastructure.database.entities.RatingJpaEntity;

import jakarta.persistence.criteria.Predicate;

public final class RatingSpecification {

    private RatingSpecification() {
    }

    public static Specification<RatingJpaEntity> buildFilter(RatingFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.movieId() != null && !filter.movieId().isBlank()) {
                predicates.add(cb.equal(root.get("movie").get("id"), filter.movieId()));
            }

            if (filter.userId() != null && !filter.userId().isBlank()) {
                predicates.add(cb.equal(root.get("user").get("id"), filter.userId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
