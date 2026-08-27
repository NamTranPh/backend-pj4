package com.example.backend_pj4.application.usecase.favorite;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.favorite.FavoriteMovieResult;
import com.example.backend_pj4.application.port.in.favorite.ListFavoriteMoviesUseCase;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.domain.model.ListFavorite;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.FavoriteRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class ListFavoriteMoviesService implements ListFavoriteMoviesUseCase {

    private final FavoriteRepository favoriteRepository;
    private final MovieRepository movieRepository;
    private final StorageUrlResolver storageUrlResolver;

    public ListFavoriteMoviesService(
            FavoriteRepository favoriteRepository,
            MovieRepository movieRepository,
            StorageUrlResolver storageUrlResolver
    ) {
        this.favoriteRepository = favoriteRepository;
        this.movieRepository = movieRepository;
        this.storageUrlResolver = storageUrlResolver;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteMovieResult> execute(String userId) {
        List<ListFavorite> favorites = favoriteRepository.findByUserId(userId);

        return favorites.stream()
                .map(fav -> movieRepository.findById(fav.getMovieId()).orElse(null))
                .filter(movie -> movie != null)
                .map(this::toResult)
                .toList();
    }

    private FavoriteMovieResult toResult(Movie movie) {
        return new FavoriteMovieResult(
                movie.getId(),
                movie.getSlug(),
                movie.getTitle(),
                storageUrlResolver.resolvePublicImage(movie.getPosterUrl()),
                movie.getIsPremium(),
                movie.getCreatedAt()
        );
    }
}
