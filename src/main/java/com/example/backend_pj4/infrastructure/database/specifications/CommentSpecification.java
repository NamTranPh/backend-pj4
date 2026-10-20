/**
 * Bộ lọc động cho bình luận. Một specification phục vụ cả danh sách công khai (chỉ bình luận gốc
 * đã duyệt của một phim/tập) lẫn màn kiểm duyệt của admin (phẳng, mọi trạng thái).
 */
package com.example.backend_pj4.infrastructure.database.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.backend_pj4.domain.repository.filter.CommentFilter;
import com.example.backend_pj4.infrastructure.database.entities.CommentJpaEntity;

import jakarta.persistence.criteria.Predicate;

public final class CommentSpecification {

    private CommentSpecification() {
    }

    public static Specification<CommentJpaEntity> buildFilter(CommentFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.movieId() != null && !filter.movieId().isBlank()) {
                predicates.add(cb.equal(root.get("movie").get("id"), filter.movieId()));
            }

            if (filter.rootOnly()) {
                predicates.add(cb.isNull(root.get("parent")));
            }

            if (filter.episodeScoped()) {
                if (filter.episodeId() == null || filter.episodeId().isBlank()) {
                    predicates.add(cb.isNull(root.get("episode")));
                } else {
                    predicates.add(cb.equal(root.get("episode").get("id"), filter.episodeId()));
                }
            }

            if (filter.isApproved() != null) {
                predicates.add(cb.equal(root.get("isApproved"), filter.isApproved()));
            }

            if (filter.q() != null && !filter.q().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("content")),
                        "%" + filter.q().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
