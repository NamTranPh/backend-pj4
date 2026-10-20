package com.example.backend_pj4.domain.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.repository.filter.RatingFilter;

public interface RatingRepository {
    Rating save(Rating rating);
    Optional<Rating> findById(String id);
    Optional<Rating> findByUserIdAndMovieId(String userId, String movieId);
    PageResult<Rating> find(RatingFilter filter, PageQuery query);
    void deleteByUserIdAndMovieId(String userId, String movieId);
    Double averageScoreByMovieId(String movieId);
    long countByMovieId(String movieId);
    /** Đếm số lượt đánh giá theo từng phim theo batch. */
    Map<String, Long> countByMovieIds(Collection<String> movieIds);
}
