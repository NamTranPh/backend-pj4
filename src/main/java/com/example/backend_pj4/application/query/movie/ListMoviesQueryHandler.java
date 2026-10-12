package com.example.backend_pj4.application.query.movie;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.mapper.MovieResultMapper;
import com.example.backend_pj4.application.port.in.movie.ListMoviesQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.filter.MovieFilter;

@Service
public class ListMoviesQueryHandler implements ListMoviesQuery {

    private final MovieRepository movieRepository;
    private final MovieResultMapper movieResultMapper;

    public ListMoviesQueryHandler(MovieRepository movieRepository, MovieResultMapper movieResultMapper) {
        this.movieRepository = movieRepository;
        this.movieResultMapper = movieResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<MovieResult> execute(MovieFilter filter, PageQuery query) {
        return movieRepository.find(filter, query).map(movieResultMapper::toResult);
    }
}
