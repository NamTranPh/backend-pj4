package com.example.backend_pj4.application.query.rating;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.rating.MyRatingResult;
import com.example.backend_pj4.application.port.in.rating.ListMyRatingsQuery;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.domain.repository.filter.RatingFilter;

@Service
public class ListMyRatingsQueryHandler implements ListMyRatingsQuery {

    private final RatingRepository ratingRepository;
    private final MovieRepository movieRepository;
    private final StorageUrlResolver storageUrlResolver;

    public ListMyRatingsQueryHandler(RatingRepository ratingRepository,
                                     MovieRepository movieRepository,
                                     StorageUrlResolver storageUrlResolver) {
        this.ratingRepository = ratingRepository;
        this.movieRepository = movieRepository;
        this.storageUrlResolver = storageUrlResolver;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<MyRatingResult> execute(String userId, PageQuery query) {
        PageResult<Rating> page = ratingRepository.find(new RatingFilter(null, userId), query);
        if (page.items().isEmpty()) {
            return page.withItems(List.of());
        }

        List<String> movieIds = page.items().stream()
                .map(Rating::getMovieId)
                .distinct()
                .toList();

        Map<String, Movie> moviesById = movieRepository.findByIds(movieIds).stream()
                .collect(Collectors.toMap(Movie::getId, Function.identity()));

        List<MyRatingResult> results = page.items().stream()
                .map(rating -> {
                    Movie movie = moviesById.get(rating.getMovieId());
                    String movieSlug = movie != null ? movie.getSlug() : null;
                    String movieTitle = movie != null ? movie.getTitle() : null;
                    String posterUrl = movie != null ? storageUrlResolver.resolvePublicImage(movie.getPosterUrl()) : null;

                    return new MyRatingResult(
                            rating.getId(),
                            rating.getMovieId(),
                            movieSlug,
                            movieTitle,
                            posterUrl,
                            rating.getScore() != null ? rating.getScore() : 0,
                            rating.getReview(),
                            rating.getCreatedAt(),
                            rating.getUpdatedAt()
                    );
                })
                .toList();

        return page.withItems(results);
    }
}
