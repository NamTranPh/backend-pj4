package com.example.backend_pj4.application.query.movie;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.mapper.MovieResultMapper;
import com.example.backend_pj4.application.port.in.movie.GetRelatedMoviesQuery;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class GetRelatedMoviesQueryHandler implements GetRelatedMoviesQuery {

    private final MovieRepository movieRepository;
    private final MovieResultMapper movieResultMapper;

    public GetRelatedMoviesQueryHandler(MovieRepository movieRepository, MovieResultMapper movieResultMapper) {
        this.movieRepository = movieRepository;
        this.movieResultMapper = movieResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieResult> execute(String slug, int limit) {
        Movie movie = movieRepository.findBySlug(slug)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        int safeLimit = Math.max(1, Math.min(limit, 30));
        List<String> genreIds = movie.getGenres() != null
                ? movie.getGenres().stream().map(Genre::getId).toList()
                : Collections.emptyList();

        List<Movie> related = movieRepository.findRelatedMovies(movie.getId(), genreIds, safeLimit);
        return related.stream().map(movieResultMapper::toResult).toList();
    }
}
