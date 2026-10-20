package com.example.backend_pj4.application.query.rating;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.rating.RatingResult;
import com.example.backend_pj4.application.mapper.RatingResultMapper;
import com.example.backend_pj4.application.port.in.rating.ListRatingsQuery;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.domain.repository.filter.RatingFilter;

@Service
public class ListRatingsQueryHandler implements ListRatingsQuery {

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final RatingResultMapper ratingResultMapper;

    public ListRatingsQueryHandler(MovieRepository movieRepository,
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
    public PageResult<RatingResult> execute(String movieSlug, PageQuery query) {
        Movie movie = movieRepository.findBySlug(movieSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        PageResult<Rating> page = ratingRepository.find(new RatingFilter(movie.getId(), null), query);
        if (page.items().isEmpty()) {
            return page.withItems(List.of());
        }

        // Nạp tác giả một lần cho cả trang thay vì findById trong vòng lặp.
        Map<String, User> authorsById = userRepository
                .findByIds(page.items().stream().map(Rating::getUserId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return page.withItems(page.items().stream()
                .map(rating -> ratingResultMapper.toResult(rating, authorsById.get(rating.getUserId())))
                .toList());
    }
}
