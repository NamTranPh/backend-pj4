package com.example.backend_pj4.application.port.in.movie;

import org.springframework.data.domain.Page;

import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;

public interface ListMoviesUseCase {
    Page<MovieResult> execute(String search, MovieType movieType, VideoStatus status,
                              String genreId, Integer year, String country,
                              int page, int size, boolean publicOnly);
}
