package com.example.backend_pj4.application.query.rating;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.rating.RatingResult;
import com.example.backend_pj4.application.mapper.RatingResultMapper;
import com.example.backend_pj4.application.port.in.rating.GetMyRatingQuery;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class GetMyRatingQueryHandler implements GetMyRatingQuery {

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final RatingResultMapper ratingResultMapper;

    public GetMyRatingQueryHandler(MovieRepository movieRepository,
                                   RatingRepository ratingRepository,
                                   UserRepository userRepository,
                                   RatingResultMapper ratingResultMapper) {
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.ratingResultMapper = ratingResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public RatingResult execute(String userId, String movieSlug) {
        Movie movie = movieRepository.findBySlug(movieSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        return ratingRepository.findByUserIdAndMovieId(userId, movie.getId())
                .map(rating -> ratingResultMapper.toResult(rating, userRepository.findById(userId).orElse(null)))
                .orElse(null);
    }
}
