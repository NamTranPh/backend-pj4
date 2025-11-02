package com.example.backend_pj4.presentation.controllers;

import java.util.List;
import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.request.genre.RequestCreateGenreDto;
import com.example.backend_pj4.application.dto.request.genre.RequestGetGenreDto;
import com.example.backend_pj4.application.dto.request.genre.RequestUpdateGenreDto;
import com.example.backend_pj4.application.dto.response.genre.GenreResponse;
import com.example.backend_pj4.application.dto.response.genre.ResponseApiArrayGenreDto;
import com.example.backend_pj4.application.dto.response.genre.ResponseApiGenreDto;
import com.example.backend_pj4.application.services.genre.CreateGenreService;
import com.example.backend_pj4.application.services.genre.DeleteGenreService;
import com.example.backend_pj4.application.services.genre.GetGenreService;
import com.example.backend_pj4.application.services.genre.UpdateGenreService;
import com.example.backend_pj4.common.base.BaseController;
import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.common.dto.response.PaginationDto;
import com.example.backend_pj4.domain.entities.Genre;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
@Validated
@Tag(name = "Genres")
@SecurityRequirement(name = "bearerAuth")
public class GenreController extends BaseController {

    private final CreateGenreService createGenreService;
    private final GetGenreService getGenreService;
    private final UpdateGenreService updateGenreUseCase;
    private final DeleteGenreService deleteGenreService;

    // private static final Logger logger = LoggerFactory.getLogger(GenreController.class);

    @GetMapping
    @Operation(summary = "Get all genres")
    public ResponseApiArrayGenreDto getAll(@ParameterObject @ModelAttribute RequestGetGenreDto dto) {
        var result = getGenreService.execute(dto);

        var data = (List<Genre>) result.get("data");
        var paginationMap = (Map<String, Object>) result.get("pagination");

        var pagination = PaginationDto.builder()
                .page((int) paginationMap.get("page"))
                .limit((int) paginationMap.get("limit"))
                .totalItems((long) paginationMap.get("totalItems"))
                .totalPages((int) paginationMap.get("totalPages"))
                .build();

        var responseData = data.stream()
                .map(GenreResponse::fromDomain)
                .toList();

        return ResponseApiArrayGenreDto.of("Get all genres successfully", responseData, pagination);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get genre by id")
    public ResponseApiGenreDto getById(@PathVariable String id) {
        Genre genre = getGenreService.executeSingle(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));
        return ResponseApiGenreDto.of("Get genre successfully", GenreResponse.fromDomain(genre));
    }

    @PostMapping
    @Operation(summary = "Create a new genre")
    public ResponseApiGenreDto create(@RequestBody RequestCreateGenreDto dto) {
        Genre genre = createGenreService.execute(dto);
        return ResponseApiGenreDto.of("Genre created successfully", GenreResponse.fromDomain(genre));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a genre by ID")
    public ResponseApiGenreDto update(@PathVariable String id, @RequestBody RequestUpdateGenreDto dto) {
        Genre updated = updateGenreUseCase.execute(id, dto);
        return ResponseApiGenreDto.of("Genre updated successfully", GenreResponse.fromDomain(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a genre by ID")
    public ApiResponseDto<Void> delete(@PathVariable String id) {
        deleteGenreService.execute(id);
        return ApiResponseDto.success(null, "Genre deleted successfully");
    }
}
