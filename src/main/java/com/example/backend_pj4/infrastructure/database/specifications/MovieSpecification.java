/**
 * Xây dựng bộ lọc dữ liệu động (Dynamic Specification Builder) cho Phim
 * Hỗ trợ tìm kiếm theo tiêu đề, lọc theo loại phim, trạng thái video, thể loại, năm phát hành và quốc gia.
 */
package com.example.backend_pj4.infrastructure.database.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;
import com.example.backend_pj4.domain.repository.filter.MovieFilter;
import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;

import jakarta.persistence.criteria.Predicate;

public final class MovieSpecification {

    private MovieSpecification() {
    }

    public static Specification<MovieJpaEntity> buildFilter(MovieFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.publicOnly()) {
                predicates.add(cb.equal(root.get("status"), VideoStatus.READY));
                predicates.add(cb.equal(root.get("visibility"), VideoVisibility.PUBLIC));
            }

            if (filter.q() != null && !filter.q().isBlank()) {
                String pattern = "%" + filter.q().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("originalTitle")), pattern)
                ));
            }

            if (filter.movieType() != null) {
                predicates.add(cb.equal(root.get("movieType"), filter.movieType()));
            }

            if (filter.status() != null && !filter.publicOnly()) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }

            if (filter.genreId() != null && !filter.genreId().isBlank()) {
                predicates.add(cb.equal(root.join("genres").get("id"), filter.genreId()));
                if (query != null) {
                    query.distinct(true);
                }
            }

            if (filter.year() != null) {
                predicates.add(cb.equal(root.get("releaseYear"), filter.year()));
            }

            if (filter.country() != null && !filter.country().isBlank()) {
                predicates.add(cb.equal(root.get("country"), filter.country()));
            }

            if (filter.ageRating() != null) {
                predicates.add(cb.equal(root.get("ageRating"), filter.ageRating()));
            }

            if (filter.quality() != null) {
                predicates.add(cb.like(root.get("videoQualities").as(String.class), "%" + filter.quality().name() + "%"));
            }

            if (filter.audioTrack() != null) {
                predicates.add(cb.like(root.get("audioTracks").as(String.class), "%" + filter.audioTrack().name() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<MovieJpaEntity> buildFilter(
            String search, MovieType movieType, VideoStatus status,
            String genreId, Integer year, String country, boolean publicOnly
    ) {
        return buildFilter(new MovieFilter(search, movieType, status, genreId, year, country, publicOnly));
    }
}
