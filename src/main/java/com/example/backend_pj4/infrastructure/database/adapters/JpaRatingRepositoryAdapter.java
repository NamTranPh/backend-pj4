package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.infrastructure.database.mappers.RatingPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.RatingJpaRepository;

@Repository
public class JpaRatingRepositoryAdapter implements RatingRepository {

    private final RatingJpaRepository ratingJpaRepository;
    private final RatingPersistenceMapper ratingPersistenceMapper;

    public JpaRatingRepositoryAdapter(RatingJpaRepository ratingJpaRepository, RatingPersistenceMapper ratingPersistenceMapper) {
        this.ratingJpaRepository = ratingJpaRepository;
        this.ratingPersistenceMapper = ratingPersistenceMapper;
    }

    @Override
    public Rating save(Rating rating) {
        return ratingPersistenceMapper.toDomain(ratingJpaRepository.save(ratingPersistenceMapper.toEntity(rating)));
    }

    @Override
    public Optional<Rating> findById(String id) {
        return ratingJpaRepository.findById(id).map(ratingPersistenceMapper::toDomain);
    }

    @Override
    public List<Rating> findAll() {
        return ratingJpaRepository.findAll().stream().map(ratingPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        ratingJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Rating> findByUserIdAndMovieId(String userId, String movieId) {
        return ratingJpaRepository.findByUser_IdAndMovie_Id(userId, movieId).map(ratingPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndMovieId(String userId, String movieId) {
        return ratingJpaRepository.existsByUser_IdAndMovie_Id(userId, movieId);
    }

    @Override
    public List<Rating> findByMovieId(String movieId) {
        return ratingJpaRepository.findByMovie_Id(movieId).stream().map(ratingPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Rating> findByUserId(String userId) {
        return ratingJpaRepository.findByUser_Id(userId).stream().map(ratingPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Double calculateAverageByMovieId(String movieId) {
        return ratingJpaRepository.calculateAverageByMovieId(movieId);
    }

    @Override
    public long countByMovieId(String movieId) {
        return ratingJpaRepository.countByMovie_Id(movieId);
    }
}


