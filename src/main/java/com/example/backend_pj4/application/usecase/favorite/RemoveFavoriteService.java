package com.example.backend_pj4.application.usecase.favorite;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.favorite.RemoveFavoriteUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.FavoriteRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class RemoveFavoriteService implements RemoveFavoriteUseCase {

    private final FavoriteRepository favoriteRepository;
    private final MovieRepository movieRepository;

    public RemoveFavoriteService(FavoriteRepository favoriteRepository, MovieRepository movieRepository) {
        this.favoriteRepository = favoriteRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    @Transactional
    public void execute(String userId, String movieSlug) {
        Movie movie = movieRepository.findBySlug(movieSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        if (!favoriteRepository.existsByUserIdAndMovieId(userId, movie.getId())) {
            throw new CustomException(ErrorCode.FAVORITE_NOT_FOUND);
        }

        favoriteRepository.deleteByUserIdAndMovieId(userId, movie.getId());
    }
}
