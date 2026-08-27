package com.example.backend_pj4.application.port.in.favorite;

import java.util.List;

import com.example.backend_pj4.application.dto.favorite.FavoriteMovieResult;

public interface ListFavoriteMoviesUseCase {
    List<FavoriteMovieResult> execute(String userId);
}
