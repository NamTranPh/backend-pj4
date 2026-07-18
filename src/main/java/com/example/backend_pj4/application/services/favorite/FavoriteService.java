package com.example.backend_pj4.application.services.favorite;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.entities.ListFavorite;
import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.FavoriteRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService extends BaseService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public ListFavorite addToFavorite(String userId, String movieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        if (favoriteRepository.existsByUserAndMovie(userId, movieId)) {
            throw new IllegalArgumentException("Movie already in favorites");
        }

        ListFavorite favorite = ListFavorite.builder().build();
        favorite.setUser(user);
        favorite.setMovie(movie);

        return favoriteRepository.save(favorite);
    }

    public void removeFromFavorite(String userId, String movieId) {
        Optional<ListFavorite> favorite = favoriteRepository.findByUserAndMovie(userId, movieId);
        if (favorite.isEmpty()) {
            throw new ResourceNotFoundException("Favorite not found");
        }
        favoriteRepository.deleteById(favorite.get().getFavoriteId());
    }

    @Transactional(readOnly = true)
    public List<ListFavorite> getUserFavorites(String userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(String userId, String movieId) {
        return favoriteRepository.existsByUserAndMovie(userId, movieId);
    }

    @Transactional(readOnly = true)
    public long countUserFavorites(String userId) {
        return favoriteRepository.countByUserId(userId);
    }

    public void clearUserFavorites(String userId) {
        List<ListFavorite> favorites = favoriteRepository.findByUserId(userId);
        for (ListFavorite fav : favorites) {
            favoriteRepository.deleteById(fav.getFavoriteId());
        }
    }
}
