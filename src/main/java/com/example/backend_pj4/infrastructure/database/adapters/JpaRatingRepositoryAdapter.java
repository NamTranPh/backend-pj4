package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.infrastructure.database.mappers.RatingPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.SpringDataRatingRepository;

@Repository
public class JpaRatingRepositoryAdapter implements RatingRepository {

    private final SpringDataRatingRepository SpringDataRatingRepository;
    private final RatingPersistenceMapper RatingPersistenceMapper;

    public JpaRatingRepositoryAdapter(SpringDataRatingRepository SpringDataRatingRepository, RatingPersistenceMapper RatingPersistenceMapper) {
        this.SpringDataRatingRepository = SpringDataRatingRepository;
        this.RatingPersistenceMapper = RatingPersistenceMapper;
    }

    @Override
    public Rating save(Rating rating) {
        return RatingPersistenceMapper.toDomain(SpringDataRatingRepository.save(RatingPersistenceMapper.toEntity(rating)));
    }

    @Override
    public Optional<Rating> findById(String id) {
        return SpringDataRatingRepository.findById(id).map(RatingPersistenceMapper::toDomain);
    }

    @Override
    public List<Rating> findAll() {
        return SpringDataRatingRepository.findAll().stream().map(RatingPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        SpringDataRatingRepository.deleteById(id);
    }

    @Override
    public Optional<Rating> findByUserIdAndMovieId(String userId, String movieId) {
        return SpringDataRatingRepository.findByUser_IdAndMovie_Id(userId, movieId).map(RatingPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndMovieId(String userId, String movieId) {
        return SpringDataRatingRepository.existsByUser_IdAndMovie_Id(userId, movieId);
    }

    @Override
    public List<Rating> findByMovieId(String movieId) {
        return SpringDataRatingRepository.findByMovie_Id(movieId).stream().map(RatingPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Rating> findByUserId(String userId) {
        return SpringDataRatingRepository.findByUser_Id(userId).stream().map(RatingPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Double calculateAverageByMovieId(String movieId) {
        return SpringDataRatingRepository.calculateAverageByMovieId(movieId);
    }

    @Override
    public long countByMovieId(String movieId) {
        return SpringDataRatingRepository.countByMovie_Id(movieId);
    }
}


