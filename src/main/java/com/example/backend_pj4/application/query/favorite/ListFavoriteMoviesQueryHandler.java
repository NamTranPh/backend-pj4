package com.example.backend_pj4.application.query.favorite;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.favorite.FavoriteMovieResult;
import com.example.backend_pj4.application.port.in.favorite.ListFavoriteMoviesQuery;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.ListFavorite;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.FavoriteRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.filter.FavoriteFilter;

@Service
public class ListFavoriteMoviesQueryHandler implements ListFavoriteMoviesQuery {

    private final FavoriteRepository favoriteRepository;
    private final MovieRepository movieRepository;
    private final StorageUrlResolver storageUrlResolver;

    public ListFavoriteMoviesQueryHandler(FavoriteRepository favoriteRepository,
                                          MovieRepository movieRepository,
                                          StorageUrlResolver storageUrlResolver) {
        this.favoriteRepository = favoriteRepository;
        this.movieRepository = movieRepository;
        this.storageUrlResolver = storageUrlResolver;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<FavoriteMovieResult> execute(FavoriteFilter filter, PageQuery query) {
        PageResult<ListFavorite> page = favoriteRepository.find(filter, query);
        if (page.items().isEmpty()) {
            return page.withItems(List.of());
        }

        // Nạp một lần thay vì findById trong vòng lặp: 300 phim yêu thích từng là 301 truy vấn.
        Map<String, Movie> moviesById = movieRepository
                .findByIds(page.items().stream().map(ListFavorite::getMovieId).toList())
                .stream()
                .collect(Collectors.toMap(Movie::getId, movie -> movie));

        // withItems thay cho map() vì phải loại dòng trỏ tới phim đã xoá, số phần tử thay đổi
        return page.withItems(page.items().stream()
                .filter(fav -> moviesById.containsKey(fav.getMovieId()))
                .map(fav -> toResult(fav, moviesById.get(fav.getMovieId())))
                .toList());
    }

    private FavoriteMovieResult toResult(ListFavorite favorite, Movie movie) {
        return new FavoriteMovieResult(
                movie.getId(),
                movie.getSlug(),
                movie.getTitle(),
                storageUrlResolver.resolvePublicImage(movie.getPosterUrl()),
                movie.getIsPremium(),
                // addedAt = ngày THÊM VÀO YÊU THÍCH, không phải ngày tạo phim
                favorite.getCreatedAt());
    }
}
