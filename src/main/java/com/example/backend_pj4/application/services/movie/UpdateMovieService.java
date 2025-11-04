package com.example.backend_pj4.application.services.movie;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.movie.RequestUpdateMovieDto;
import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateMovieService extends BaseService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    public Movie execute(String movieId, RequestUpdateMovieDto request) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim với ID: " + movieId));

        // ✅ Không cho đổi movieType nếu đã có tập
        if (request.getMovieType() != null && request.getMovieType() != movie.getMovieType()) {
            throw new IllegalArgumentException("Không thể thay đổi loại phim sau khi tạo.");
        }

        movie.setTitle(request.getTitle() != null ? request.getTitle() : movie.getTitle());
        movie.setOriginalTitle(
                request.getOriginalTitle() != null ? request.getOriginalTitle() : movie.getOriginalTitle());
        movie.setDescription(request.getDescription() != null ? request.getDescription() : movie.getDescription());
        movie.setReleaseYear(request.getReleaseYear() != null ? request.getReleaseYear() : movie.getReleaseYear());
        movie.setDuration(request.getDuration() != null ? request.getDuration() : movie.getDuration());
        movie.setDirector(request.getDirector() != null ? request.getDirector() : movie.getDirector());
        movie.setActors(request.getActors() != null ? request.getActors() : movie.getActors());
        movie.setCountry(request.getCountry() != null ? request.getCountry() : movie.getCountry());
        movie.setLanguage(request.getLanguage() != null ? request.getLanguage() : movie.getLanguage());
        movie.setTrailerUrl(request.getTrailerUrl() != null ? request.getTrailerUrl() : movie.getTrailerUrl());
        movie.setPosterUrl(request.getPosterUrl() != null ? request.getPosterUrl() : movie.getPosterUrl());
        movie.setBackdropUrl(request.getBackdropUrl() != null ? request.getBackdropUrl() : movie.getBackdropUrl());
        movie.setStatus(request.getStatus() != null ? request.getStatus() : movie.getStatus());
        movie.setIsPremium(request.getIsPremium() != null ? request.getIsPremium() : movie.getIsPremium());
        movie.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : movie.getIsFeatured());
        movie.setIsActive(request.getIsActive() != null ? request.getIsActive() : movie.getIsActive());

        // ✅ Cập nhật genre nếu có gửi mới
        if (request.getGenreIds() != null) {
            var genres = genreRepository.findAllByIds(request.getGenreIds());
            if (genres.isEmpty()) {
                throw new ResourceNotFoundException("Không tìm thấy thể loại hợp lệ");
            }
            movie.setGenres(genres);
        }

        return movieRepository.save(movie);
    }
}