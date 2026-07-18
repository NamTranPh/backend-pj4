package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.entities.Rating;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.infrastructure.databases.entities.RatingEntity;
import com.example.backend_pj4.infrastructure.databases.mapper.RatingMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaRatingRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RatingRepositoryImpl implements RatingRepository {

    private final JpaRatingRepository jpaRatingRepository;
    private final RatingMapper ratingMapper;

    @Override
    public Rating save(Rating rating) {
        var entity = ratingMapper.toEntity(rating);
        var saved = jpaRatingRepository.save(entity);
        return ratingMapper.toDomain(saved);
    }

    @Override
    public Optional<Rating> findById(String ratingId) {
        return jpaRatingRepository.findById(ratingId)
                .map(ratingMapper::toDomain);
    }

    @Override
    public List<Rating> findAll() {
        return jpaRatingRepository.findAll().stream()
                .map(ratingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String ratingId) {
        jpaRatingRepository.deleteById(ratingId);
    }

    @Override
    public Optional<Rating> findByUserIdAndMovieId(String userId, String movieId) {
        return jpaRatingRepository.findByUser_UserIdAndMovie_MovieId(userId, movieId)
                .map(ratingMapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndMovieId(String userId, String movieId) {
        return jpaRatingRepository.existsByUser_UserIdAndMovie_MovieId(userId, movieId);
    }

    @Override
    public List<Rating> findByMovieId(String movieId) {
        return jpaRatingRepository.findByMovie_MovieId(movieId).stream()
                .map(ratingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Rating> findByUserId(String userId) {
        return jpaRatingRepository.findByUser_UserId(userId).stream()
                .map(ratingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Rating> findByMovieIdOrderByScoreDesc(String movieId) {
        return jpaRatingRepository.findByMovie_MovieId(movieId).stream()
                .sorted((a, b) -> {
                    if (a.getScore() == null && b.getScore() == null) return 0;
                    if (a.getScore() == null) return 1;
                    if (b.getScore() == null) return -1;
                    return b.getScore().compareTo(a.getScore());
                })
                .map(ratingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Rating> findByMovieIdAndScoreGreaterThan(String movieId, Integer score) {
        return jpaRatingRepository.findByMovie_MovieId(movieId).stream()
                .filter(r -> r.getScore() != null && r.getScore() > score)
                .map(ratingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Rating> findByUserIdOrderByCreatedAtDesc(String userId) {
        return jpaRatingRepository.findByUser_UserId(userId).stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .map(ratingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateAverageRatingByMovieId(String movieId) {
        return jpaRatingRepository.findAverageRatingByMovieId(movieId);
    }

    @Override
    public long countByMovieId(String movieId) {
        return jpaRatingRepository.countRatingsByMovieId(movieId);
    }

    @Override
    public long countByScore(Integer score) {
        return jpaRatingRepository.findAll().stream()
                .filter(r -> r.getScore() != null && r.getScore().equals(score))
                .count();
    }

    @Override
    public List<Object[]> findTopRatedMovies(int limit) {
        return jpaRatingRepository.findAll().stream()
                .filter(r -> r.getMovie() != null)
                .collect(Collectors.groupingBy(r -> r.getMovie().getMovieId()))
                .entrySet().stream()
                .map(entry -> {
                    double avg = entry.getValue().stream()
                            .filter(r -> r.getScore() != null)
                            .mapToInt(RatingEntity::getScore)
                            .average()
                            .orElse(0.0);
                    return new Object[]{entry.getKey(), avg, (long) entry.getValue().size()};
                })
                .sorted((a, b) -> Double.compare((Double) b[1], (Double) a[1]))
                .limit(limit)
                .toList();
    }
}
