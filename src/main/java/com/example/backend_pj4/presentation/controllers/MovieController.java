package com.example.backend_pj4.presentation.controllers;

import java.util.List;
import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.request.episode.RequestCreateEpisodeDto;
import com.example.backend_pj4.application.dto.request.episode.RequestUpdateEpisodeDto;
import com.example.backend_pj4.application.dto.request.movie.RequestCreateMovieDto;
import com.example.backend_pj4.application.dto.request.movie.RequestGetMovieCmsDto;
import com.example.backend_pj4.application.dto.request.movie.RequestUpdateMovieDto;
import com.example.backend_pj4.application.dto.response.episode.EpisodeResponse;
import com.example.backend_pj4.application.dto.response.episode.ResponseApiArrayEpisodeDto;
import com.example.backend_pj4.application.dto.response.episode.ResponseApiEpisodeDto;
import com.example.backend_pj4.application.dto.response.movie.MovieResponse;
import com.example.backend_pj4.application.dto.response.movie.ResponseApiArrayMovieDto;
import com.example.backend_pj4.application.dto.response.movie.ResponseApiMovieDto;
import com.example.backend_pj4.application.services.movie.CreateEpisodeService;
import com.example.backend_pj4.application.services.movie.CreateMovieService;
import com.example.backend_pj4.application.services.movie.DeleteEpisodeService;
import com.example.backend_pj4.application.services.movie.DeleteMovieService;
import com.example.backend_pj4.application.services.movie.GetEpisodeService;
import com.example.backend_pj4.application.services.movie.GetMovieService;
import com.example.backend_pj4.application.services.movie.UpdateEpisodeService;
import com.example.backend_pj4.application.services.movie.UpdateMovieService;
import com.example.backend_pj4.common.base.BaseController;
import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.common.dto.response.PaginationDto;
import com.example.backend_pj4.domain.entities.Movie;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/movie")
@RequiredArgsConstructor
@Validated
@Tag(name = "Movie")
public class MovieController extends BaseController {
    private final GetMovieService getMovieService;
    private final CreateMovieService createMovieService;
    private final UpdateMovieService updateMovieService;
    private final DeleteMovieService deleteMovieService;

    // Episode
    private final GetEpisodeService getEpisodeService;
    private final CreateEpisodeService createEpisodeService;
    private final UpdateEpisodeService updateEpisodeService;
    private final DeleteEpisodeService deleteEpisodeService;

    // ======================================================
    // 🎬 MOVIES
    // ======================================================
    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get all movies")
    public ResponseApiArrayMovieDto getMovies(@ParameterObject @ModelAttribute RequestGetMovieCmsDto dto) {
        var result = getMovieService.execute(dto);

        var data = (List<MovieResponse>) result.get("data");
        var paginationMap = (Map<String, Object>) result.get("pagination");

        var pagination = PaginationDto.builder()
                .page((int) paginationMap.get("page"))
                .limit((int) paginationMap.get("limit"))
                .totalItems((long) paginationMap.get("totalItems"))
                .totalPages((int) paginationMap.get("totalPages"))
                .build();

        return ResponseApiArrayMovieDto.of("Get all movies successfully", data, pagination);
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Create movie", description = "Create new movie")
    public ResponseApiMovieDto create(@RequestBody RequestCreateMovieDto dto) {
        Movie movie = createMovieService.execute(dto);
        return ResponseApiMovieDto.of("User created successfully", MovieResponse.fromDomain(movie));
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get movie by ID")
    public ResponseApiMovieDto getById(@PathVariable String id) {
        Movie movie = getMovieService.executeSingle(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        return ResponseApiMovieDto.of("Get movie successfully", MovieResponse.fromDomain(movie));
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Update movie", description = "Update movie information")
    public ResponseApiMovieDto update(@PathVariable String id, @RequestBody RequestUpdateMovieDto dto) {
        Movie updated = updateMovieService.execute(id, dto);
        return ResponseApiMovieDto.of("Movie updated successfully", MovieResponse.fromDomain(updated));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @Operation(summary = "Delete movie", description = "Delete a movie by ID")
    public ApiResponseDto<Void> delete(@PathVariable String id) {
        deleteMovieService.execute(id);
        return ApiResponseDto.success(null, "Movie deleted successfully");
    }

    // ======================================================
    // 📺 EPISODES (CRUD under movie)
    // ======================================================
    @GetMapping("/{movieId}/episodes")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get episodes of a movie by movieId (with pagination)")
    public ResponseApiArrayEpisodeDto getEpisodes(
            @PathVariable String movieId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {

        var result = getEpisodeService.execute(movieId, page, limit);
        var data = (List<EpisodeResponse>) result.get("data");
        var paginationMap = (Map<String, Object>) result.get("pagination");

        var pagination = PaginationDto.builder()
                .page((int) paginationMap.get("page"))
                .limit((int) paginationMap.get("limit"))
                .totalItems((long) paginationMap.get("totalItems"))
                .totalPages((int) paginationMap.get("totalPages"))
                .build();

        return ResponseApiArrayEpisodeDto.of("Get episodes successfully", data, pagination);
    }

    @PostMapping("/{movieId}/episodes")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseApiEpisodeDto createEpisode(
            @PathVariable String movieId,
            @RequestBody RequestCreateEpisodeDto dto) {
        var episode = createEpisodeService.execute(movieId, dto);
        return ResponseApiEpisodeDto.of("Episode created successfully", EpisodeResponse.fromDomain(episode));
    }

    @PatchMapping("/{movieId}/episodes/{episodeId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseApiEpisodeDto updateEpisode(
            @PathVariable String movieId,
            @PathVariable String episodeId,
            @RequestBody RequestUpdateEpisodeDto dto) {
        var episode = updateEpisodeService.execute(episodeId, dto);
        return ResponseApiEpisodeDto.of("Episode updated successfully", EpisodeResponse.fromDomain(episode));
    }

    @DeleteMapping("/{movieId}/episodes/{episodeId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ApiResponseDto<Void> deleteEpisode(
            @PathVariable String movieId,
            @PathVariable String episodeId) {
        deleteEpisodeService.execute(movieId, episodeId);
        return ApiResponseDto.success(null, "Episode deleted successfully");
    }

}
