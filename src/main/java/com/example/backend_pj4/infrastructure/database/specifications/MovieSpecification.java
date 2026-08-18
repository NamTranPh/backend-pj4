package com.example.backend_pj4.infrastructure.database.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;
import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;

import jakarta.persistence.criteria.Predicate;

public final class MovieSpecification {

    private MovieSpecification() {
    }

    public static Specification<MovieJpaEntity> buildFilter(
            String search, MovieType movieType, VideoStatus status,
            String genreId, Integer year, String country, boolean publicOnly
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (publicOnly) {
                predicates.add(cb.equal(root.get("status"), VideoStatus.READY));
                predicates.add(cb.equal(root.get("visibility"), VideoVisibility.PUBLIC));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("originalTitle")), pattern)
                ));
            }

            if (movieType != null) {
                predicates.add(cb.equal(root.get("movieType"), movieType));
            }

            if (status != null && !publicOnly) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (genreId != null && !genreId.isBlank()) {
                predicates.add(cb.equal(root.join("genres").get("id"), genreId));
                if (query != null) {
                    query.distinct(true);
                }
            }

            if (year != null) {
                predicates.add(cb.equal(root.get("releaseYear"), year));
            }

            if (country != null && !country.isBlank()) {
                predicates.add(cb.equal(root.get("country"), country));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
