package com.example.backend_pj4.application.usecase.movie;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.actor.MovieActorAssignmentCommand;
import com.example.backend_pj4.application.command.movie.UpdateMovieCommand;
import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.mapper.MovieResultMapper;
import com.example.backend_pj4.application.port.in.movie.UpdateMovieUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.util.SlugUtils;
import com.example.backend_pj4.domain.model.Actor;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.MovieActor;
import com.example.backend_pj4.domain.repository.ActorRepository;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class UpdateMovieService implements UpdateMovieUseCase {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;
    private final MovieResultMapper movieResultMapper;

    public UpdateMovieService(MovieRepository movieRepository,
                              GenreRepository genreRepository,
                              ActorRepository actorRepository,
                              MovieResultMapper movieResultMapper) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
        this.movieResultMapper = movieResultMapper;
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
        if (command.country() != null) builder.country(command.country());
        if (command.language() != null) builder.language(command.language());
        if (command.trailerUrl() != null) builder.trailerUrl(command.trailerUrl());
        if (command.movieType() != null) builder.movieType(command.movieType());
        if (command.totalEpisodes() != null) builder.totalEpisodes(command.totalEpisodes());
        if (command.ageRating() != null) builder.ageRating(command.ageRating());
        if (command.videoQualities() != null && !command.videoQualities().isEmpty()) builder.videoQualities(command.videoQualities());
        if (command.audioTracks() != null && !command.audioTracks().isEmpty()) builder.audioTracks(command.audioTracks());
        if (command.isFeatured() != null) builder.isFeatured(command.isFeatured());
        if (command.isPremium() != null) builder.isPremium(command.isPremium());
        if (command.status() != null) {
            if (command.status() == VideoStatus.READY
                    || command.status() == VideoStatus.PROCESSING) {
                throw new CustomException(ErrorCode.INVALID_STATUS_TRANSITION);
            }
            builder.status(command.status());
        }
        if (command.visibility() != null) builder.visibility(command.visibility());

        if (command.genreIds() != null) {
            List<Genre> genres = genreRepository.findByIds(command.genreIds());
            if (genres.size() != command.genreIds().size()) {
                throw new CustomException(ErrorCode.INVALID_GENRE_IDS);
            }
            builder.genres(genres);
        }

        if (command.movieActors() != null) {
            List<String> actorIds = command.movieActors().stream()
                    .map(MovieActorAssignmentCommand::actorId)
                    .distinct()
                    .toList();
            List<Actor> foundActors = actorRepository.findByIds(actorIds);
            if (foundActors.size() != actorIds.size()) {
                throw new CustomException(ErrorCode.INVALID_ACTOR_IDS);
            }
            Map<String, Actor> actorMap = foundActors.stream()
                    .collect(Collectors.toMap(Actor::getId, a -> a));

            List<MovieActor> movieActors = command.movieActors().stream().map(a -> MovieActor.builder()
                    .movieId(movie.getId())
                    .actorId(a.actorId())
                    .actor(actorMap.get(a.actorId()))
                    .characterName(a.characterName())
                    .orderIndex(a.orderIndex() != null ? a.orderIndex() : 0)
                    .build()
            ).toList();

            builder.movieActors(movieActors);
        }

        Movie saved = movieRepository.save(builder.build());
        return movieResultMapper.toResult(saved);
    }
}
