package com.example.backend_pj4.application.usecase.movie;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.movie.UpdateMovieCommand;
import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.mapper.MovieResultMapper;
import com.example.backend_pj4.application.port.in.movie.UpdateMovieUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.util.SlugUtils;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class UpdateMovieService implements UpdateMovieUseCase {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    public UpdateMovieService(MovieRepository movieRepository, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional
    public MovieResult execute(UpdateMovieCommand command) {
        Movie movie = movieRepository.findById(command.id())
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        Movie.MovieBuilder builder = movie.toBuilder();

        if (command.title() != null) {
            builder.title(command.title());
            String newSlug = SlugUtils.generateSlug(command.title());
            if (!newSlug.equals(movie.getSlug()) && movieRepository.existsBySlug(newSlug)) {
                newSlug = newSlug + "-" + System.currentTimeMillis();
            }
            builder.slug(newSlug);
        }
        if (command.originalTitle() != null) builder.originalTitle(command.originalTitle());
        if (command.description() != null) builder.description(command.description());
        if (command.releaseYear() != null) builder.releaseYear(command.releaseYear());
        if (command.duration() != null) builder.duration(command.duration());
        if (command.director() != null) builder.director(command.director());
        if (command.actors() != null) builder.actors(command.actors());
        if (command.country() != null) builder.country(command.country());
        if (command.language() != null) builder.language(command.language());
        if (command.trailerUrl() != null) builder.trailerUrl(command.trailerUrl());
        if (command.movieType() != null) builder.movieType(command.movieType());
        if (command.totalEpisodes() != null) builder.totalEpisodes(command.totalEpisodes());
        if (command.isFeatured() != null) builder.isFeatured(command.isFeatured());

        if (command.genreIds() != null) {
            List<Genre> genres = genreRepository.findAllByIds(command.genreIds());
            if (genres.size() != command.genreIds().size()) {
                throw new CustomException(ErrorCode.INVALID_GENRE_IDS);
            }
            builder.genres(genres);
        }

        Movie saved = movieRepository.save(builder.build());
        return MovieResultMapper.toResult(saved);
    }
}
