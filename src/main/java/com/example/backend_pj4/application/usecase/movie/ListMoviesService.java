package com.example.backend_pj4.application.usecase.movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.mapper.MovieResultMapper;
import com.example.backend_pj4.application.port.in.movie.ListMoviesUseCase;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class ListMoviesService implements ListMoviesUseCase {

    private final MovieRepository movieRepository;
    private final MovieResultMapper movieResultMapper;

    public ListMoviesService(MovieRepository movieRepository, MovieResultMapper movieResultMapper) {
        this.movieRepository = movieRepository;
        this.movieResultMapper = movieResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResult> execute(String search, MovieType movieType, VideoStatus status,
                                     String genreId, Integer year, String country,
                                     int page, int size, boolean publicOnly) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 50), Sort.by("createdAt").descending());
        return movieRepository.findAllFiltered(search, movieType, status, genreId, year, country, publicOnly, pageable)
                .map(movieResultMapper::toResult);
    }
}
