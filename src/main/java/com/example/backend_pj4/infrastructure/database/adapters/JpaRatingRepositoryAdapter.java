package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.domain.repository.filter.RatingFilter;
import com.example.backend_pj4.infrastructure.database.mappers.RatingPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.RatingJpaRepository;
import com.example.backend_pj4.infrastructure.database.specifications.RatingSpecification;

@Repository
public class JpaRatingRepositoryAdapter implements RatingRepository {

    private final RatingJpaRepository ratingJpaRepository;
    private final RatingPersistenceMapper ratingPersistenceMapper;

    public JpaRatingRepositoryAdapter(RatingJpaRepository ratingJpaRepository,
                                      RatingPersistenceMapper ratingPersistenceMapper) {
        this.ratingJpaRepository = ratingJpaRepository;
        this.ratingPersistenceMapper = ratingPersistenceMapper;
    }

    @Override
    public Rating save(Rating rating) {
        return ratingPersistenceMapper.toDomain(
                ratingJpaRepository.save(ratingPersistenceMapper.toEntity(rating)));
    }

    @Override
    public Optional<Rating> findById(String id) {
        return ratingJpaRepository.findById(id).map(ratingPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Rating> findByUserIdAndMovieId(String userId, String movieId) {
        return ratingJpaRepository.findByUser_IdAndMovie_Id(userId, movieId)
                .map(ratingPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Rating> find(RatingFilter filter, PageQuery query) {
        return PageableMapper.toPageResult(
                ratingJpaRepository.findAll(
                        RatingSpecification.buildFilter(filter),
                        PageableMapper.toPageable(query)),
                ratingPersistenceMapper::toDomain);
    }

    @Override
    public void deleteByUserIdAndMovieId(String userId, String movieId) {
        ratingJpaRepository.deleteByUser_IdAndMovie_Id(userId, movieId);
    }

    @Override
    public Double averageScoreByMovieId(String movieId) {
        return ratingJpaRepository.calculateAverageByMovieId(movieId);
    }

    @Override
    public long countByMovieId(String movieId) {
        return ratingJpaRepository.countByMovie_Id(movieId);
    }

    @Override
    public java.util.Map<String, Long> countByMovieIds(java.util.Collection<String> movieIds) {
        if (movieIds == null || movieIds.isEmpty()) {
            return java.util.Map.of();
        }
        java.util.List<Object[]> rows = ratingJpaRepository.countGroupedByMovieIds(movieIds);
        java.util.Map<String, Long> result = new java.util.HashMap<>();
        for (Object[] row : rows) {
            String movieId = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            result.put(movieId, count);
        }
        return result;
    }
}
