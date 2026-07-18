package com.example.backend_pj4.application.services.rating;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.domain.entities.Rating;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService extends BaseService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public Rating rateMovie(String userId, String movieId, Integer score, String review) {
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException("Score must be between 1 and 10");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        Optional<Rating> existingRating = ratingRepository.findByUserIdAndMovieId(userId, movieId);
        
        if (existingRating.isPresent()) {
            Rating rating = existingRating.get();
            rating.setScore(score);
            rating.setReview(review);
            return ratingRepository.save(rating);
        }

        Rating rating = Rating.builder()
                .score(score)
                .review(review)
                .build();

        rating.setUser(user);
        rating.setMovie(movie);

        return ratingRepository.save(rating);
    }

    @Transactional(readOnly = true)
    public Optional<Rating> getUserRating(String userId, String movieId) {
        return ratingRepository.findByUserIdAndMovieId(userId, movieId);
    }

    @Transactional(readOnly = true)
    public List<Rating> getRatingsByMovie(String movieId) {
        return ratingRepository.findByMovieId(movieId);
    }

    @Transactional(readOnly = true)
    public List<Rating> getTopRatingsByMovie(String movieId) {
        return ratingRepository.findByMovieIdOrderByScoreDesc(movieId);
    }

    @Transactional(readOnly = true)
    public List<Rating> getUserRatings(String userId) {
        return ratingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(String movieId) {
        return ratingRepository.calculateAverageRatingByMovieId(movieId);
    }

    @Transactional(readOnly = true)
    public long countRatings(String movieId) {
        return ratingRepository.countByMovieId(movieId);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getTopRatedMovies(int limit) {
        return ratingRepository.findTopRatedMovies(limit);
    }

    public void deleteRating(String ratingId) {
        if (ratingRepository.findById(ratingId).isEmpty()) {
            throw new ResourceNotFoundException("Rating not found");
        }
        ratingRepository.deleteById(ratingId);
    }

    public void deleteUserMovieRating(String userId, String movieId) {
        Optional<Rating> rating = ratingRepository.findByUserIdAndMovieId(userId, movieId);
        rating.ifPresent(r -> ratingRepository.deleteById(r.getRatingId()));
    }
}
