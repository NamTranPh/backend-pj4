package com.example.backend_pj4.application.services.movie;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.request.episode.RequestCreateEpisodeDto;
import com.example.backend_pj4.application.dto.request.movie.RequestCreateMovieDto;
import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.common.enums.MovieStatus;
import com.example.backend_pj4.common.enums.MovieType;
import com.example.backend_pj4.domain.entities.Genre;
import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateMovieService extends BaseService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final CreateEpisodeService createEpisodeService;

    public Movie execute(RequestCreateMovieDto request) {
        // ✅ Mặc định SINGLE nếu không truyền
        MovieType type = request.getMovieType() != null ? request.getMovieType() : MovieType.SINGLE;

        // ✅ Không cho truyền totalEpisodes tay
        Integer totalEpisodes = type == MovieType.SINGLE ? 1 : 0;

        Movie movie = Movie.builder()
                .title(request.getTitle())
                .originalTitle(request.getOriginalTitle())
                .description(request.getDescription())
                .releaseYear(request.getReleaseYear())
                .duration(request.getDuration())
                .director(request.getDirector())
                .actors(request.getActors())
                .country(request.getCountry())
                .language(request.getLanguage())
                .trailerUrl(request.getTrailerUrl())
                .posterUrl(request.getPosterUrl())
                .backdropUrl(request.getBackdropUrl())
                .movieType(type)
                .totalEpisodes(totalEpisodes)
                .status(request.getStatus() != null ? request.getStatus() : MovieStatus.COMING_SOON)
                .isPremium(request.getIsPremium() != null ? request.getIsPremium() : false)
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        // Gán thể loại
        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            List<Genre> genres = genreRepository.findAllByIds(request.getGenreIds());
            if (genres.isEmpty()) {
                throw new ResourceNotFoundException("Không tìm thấy thể loại hợp lệ");
            }
            movie.setGenres(genres);
        }

        Movie savedMovie = movieRepository.save(movie);

        // Nếu là phim bộ thì thêm luôn tập
        if (type == MovieType.SERIES && request.getEpisodes() != null && !request.getEpisodes().isEmpty()) {
            for (RequestCreateEpisodeDto ep : request.getEpisodes()) {
                createEpisodeService.execute(savedMovie.getMovieId(), ep);
            }
        }

        return savedMovie;
    }
}