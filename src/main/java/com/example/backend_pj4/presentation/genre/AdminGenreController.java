package com.example.backend_pj4.presentation.genre;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.genre.CreateGenreCommand;
import com.example.backend_pj4.application.command.genre.UpdateGenreCommand;
import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.application.port.in.genre.CreateGenreUseCase;
import com.example.backend_pj4.application.port.in.genre.DeleteGenreUseCase;
import com.example.backend_pj4.application.port.in.genre.GetGenreByIdUseCase;
import com.example.backend_pj4.application.port.in.genre.ListGenresUseCase;
import com.example.backend_pj4.application.port.in.genre.UpdateGenreUseCase;
import com.example.backend_pj4.presentation.genre.request.CreateGenreRequest;
import com.example.backend_pj4.presentation.genre.request.UpdateGenreRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/admin/genres")
public class AdminGenreController {

    private final CreateGenreUseCase createGenreUseCase;
    private final UpdateGenreUseCase updateGenreUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final ListGenresUseCase listGenresUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;

    public AdminGenreController(
            CreateGenreUseCase createGenreUseCase,
            UpdateGenreUseCase updateGenreUseCase,
            GetGenreByIdUseCase getGenreByIdUseCase,
            ListGenresUseCase listGenresUseCase,
            DeleteGenreUseCase deleteGenreUseCase
    ) {
        this.createGenreUseCase = createGenreUseCase;
        this.updateGenreUseCase = updateGenreUseCase;
        this.getGenreByIdUseCase = getGenreByIdUseCase;
        this.listGenresUseCase = listGenresUseCase;
        this.deleteGenreUseCase = deleteGenreUseCase;
    }

    @PostMapping
    public ResponseEntity<GenreResult> create(@Valid @RequestBody CreateGenreRequest request) {
        GenreResult result = createGenreUseCase.execute(
                new CreateGenreCommand(request.name(), request.icon(), request.description()));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreResult> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateGenreRequest request
    ) {
        GenreResult result = updateGenreUseCase.execute(
                new UpdateGenreCommand(id, request.name(), request.icon(), request.description(), request.status()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<GenreResult> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(getGenreByIdUseCase.execute(slug));
    }

    @GetMapping
    public Page<GenreResult> list(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return listGenresUseCase.execute(search, Math.max(0, page - 1), limit, false);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteGenreUseCase.execute(id);
        return ResponseEntity.ok().build();
    }
}
