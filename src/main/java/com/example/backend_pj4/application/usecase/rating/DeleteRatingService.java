package com.example.backend_pj4.application.usecase.rating;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.rating.DeleteRatingUseCase;
import com.example.backend_pj4.application.service.RatingRecalculator;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.RatingRepository;

@Service
public class DeleteRatingService implements DeleteRatingUseCase {

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final RatingRecalculator ratingRecalculator;

    public DeleteRatingService(MovieRepository movieRepository,
                               RatingRepository ratingRepository,
                               RatingRecalculator ratingRecalculator) {
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
        this.ratingRecalculator = ratingRecalculator;
    }

    @Override
    @Transactional
    public void execute(String userId, String movieSlug) {
        Movie movie = movieRepository.findBySlug(movieSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        if (ratingRepository.findByUserIdAndMovieId(userId, movie.getId()).isEmpty()) {
            throw new CustomException(ErrorCode.RATING_NOT_FOUND);
        }

        // Xoá cứng, không xoá mềm — xem javadoc của RatingRepository.deleteByUserIdAndMovieId.
        // Xoá mềm ở đây sẽ khiến người dùng không bao giờ chấm lại được phim này.
        ratingRepository.deleteByUserIdAndMovieId(userId, movie.getId());

        ratingRecalculator.recalculate(movie);
    }
}
