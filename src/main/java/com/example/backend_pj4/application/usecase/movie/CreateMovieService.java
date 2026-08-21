package com.example.backend_pj4.application.usecase.movie;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.movie.CreateMovieCommand;
import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.mapper.MovieResultMapper;
import com.example.backend_pj4.application.port.in.movie.CreateMovieUseCase;

import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.constants.enums.VideoVisibility;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.util.SlugUtils;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class CreateMovieService implements CreateMovieUseCase {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final MovieResultMapper movieResultMapper;

    public CreateMovieService(MovieRepository movieRepository, GenreRepository genreRepository, UserRepository userRepository, MovieResultMapper movieResultMapper) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
        this.movieResultMapper = movieResultMapper;
    }

    @Override
    @Transactional
    public MovieResult execute(CreateMovieCommand command) {
        List<Genre> genres = Collections.emptyList();
        if (command.genreIds() != null && !command.genreIds().isEmpty()) {
            genres = genreRepository.findAllByIds(command.genreIds());
            if (genres.size() != command.genreIds().size()) {
                throw new CustomException(ErrorCode.INVALID_GENRE_IDS);
            }
        }

        String slug = SlugUtils.generateSlug(command.title());
        if (movieRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Movie movie = Movie.builder()
                .slug(slug)
                .title(command.title())
                .originalTitle(command.originalTitle())
                .description(command.description())
                .releaseYear(command.releaseYear())
                .duration(command.duration())
                .director(command.director())
                .actors(command.actors())
                .country(command.country())
                .language(command.language())
                .trailerUrl(command.trailerUrl())
                .movieType(command.movieType())
                .totalEpisodes(command.totalEpisodes())
                .status(VideoStatus.DRAFT)
                .visibility(VideoVisibility.PUBLIC)
                .isPremium(false)
                .isFeatured(false)
                .viewCount(0L)
                .genres(genres)
                .createdBy(userRepository.findByEmail(command.createdByUserId())
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)))
                .build();

        Movie saved = movieRepository.save(movie);
        return movieResultMapper.toResult(saved);
    }
}
