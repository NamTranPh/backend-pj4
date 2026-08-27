package com.example.backend_pj4.presentation.episode;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.episode.PublicEpisodeResult;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Public Episodes")
@RestController
@RequestMapping("/api/v1/movies/{movieSlug}/episodes")
public class PublicEpisodeController {

    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final StorageUrlResolver storageUrlResolver;

    public PublicEpisodeController(
            MovieRepository movieRepository,
            EpisodeRepository episodeRepository,
            StorageUrlResolver storageUrlResolver
    ) {
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.storageUrlResolver = storageUrlResolver;
    }

    @Operation(summary = "Danh sách tập phim công khai.")
    @GetMapping
    public ResponseEntity<List<PublicEpisodeResult>> listEpisodes(
            @PathVariable String movieSlug
    ) {
        Movie movie = movieRepository.findBySlug(movieSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        List<PublicEpisodeResult> results = episodeRepository.findByMovieIdOrderByEpisodeNumber(movie.getId())
                .stream()
                .map(this::toPublicResult)
                .toList();

        return ResponseEntity.ok(results);
    }

    private PublicEpisodeResult toPublicResult(Episode ep) {
        return new PublicEpisodeResult(
                ep.getEpisodeNumber(),
                ep.getTitle(),
                ep.getDescription(),
                ep.getDuration(),
                storageUrlResolver.resolvePublicImage(ep.getThumbnailUrl()),
                ep.getStatus(),
                ep.getAirDate()
        );
    }
}
