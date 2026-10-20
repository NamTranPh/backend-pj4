package com.example.backend_pj4.application.query.movie;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.mapper.MovieResultMapper;
import com.example.backend_pj4.application.port.in.movie.ListMoviesQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.domain.repository.filter.MovieFilter;

@Service
public class ListMoviesQueryHandler implements ListMoviesQuery {

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final MovieResultMapper movieResultMapper;

    public ListMoviesQueryHandler(MovieRepository movieRepository,
                                  RatingRepository ratingRepository,
                                  CommentRepository commentRepository,
                                  MovieResultMapper movieResultMapper) {
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
        this.movieResultMapper = movieResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<MovieResult> execute(MovieFilter filter, PageQuery query) {
        PageResult<Movie> page = movieRepository.find(filter, query);
        if (page.items().isEmpty()) {
            return page.withItems(List.of());
        }

        List<String> movieIds = page.items().stream().map(Movie::getId).toList();
        Map<String, Long> ratingCounts = ratingRepository.countByMovieIds(movieIds);
        Map<String, Long> commentCounts = commentRepository.countByMovieIds(movieIds);

        List<MovieResult> results = page.items().stream()
                .map(m -> movieResultMapper.toResult(
                        m,
                        ratingCounts.getOrDefault(m.getId(), 0L),
                        commentCounts.getOrDefault(m.getId(), 0L)
                ))
                .toList();

        return page.withItems(results);
    }
}
