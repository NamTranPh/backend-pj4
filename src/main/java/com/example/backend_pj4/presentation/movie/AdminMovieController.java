package com.example.backend_pj4.presentation.movie;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend_pj4.application.command.movie.CreateMovieCommand;
import com.example.backend_pj4.application.command.movie.UpdateMovieCommand;
import com.example.backend_pj4.application.dto.movie.MovieDetailResult;
import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.port.in.movie.CreateMovieUseCase;
import com.example.backend_pj4.application.port.in.movie.DeleteMovieUseCase;
import com.example.backend_pj4.application.port.in.movie.GetMovieByIdUseCase;
import com.example.backend_pj4.application.port.in.movie.ListMoviesUseCase;
import com.example.backend_pj4.application.port.in.movie.RestoreMovieUseCase;
import com.example.backend_pj4.application.port.in.movie.UpdateMovieUseCase;
import com.example.backend_pj4.application.port.in.movie.UploadMovieImageUseCase;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.presentation.movie.request.CreateMovieRequest;
import com.example.backend_pj4.presentation.movie.request.UpdateMovieRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/admin/movies")
public class AdminMovieController {

    private final CreateMovieUseCase createMovieUseCase;
    private final UpdateMovieUseCase updateMovieUseCase;
    private final GetMovieByIdUseCase getMovieByIdUseCase;
    private final ListMoviesUseCase listMoviesUseCase;
    private final DeleteMovieUseCase deleteMovieUseCase;
    private final RestoreMovieUseCase restoreMovieUseCase;
    private final UploadMovieImageUseCase uploadMovieImageUseCase;

    public AdminMovieController(
            CreateMovieUseCase createMovieUseCase,
            UpdateMovieUseCase updateMovieUseCase,
            GetMovieByIdUseCase getMovieByIdUseCase,
            ListMoviesUseCase listMoviesUseCase,
            DeleteMovieUseCase deleteMovieUseCase,
            RestoreMovieUseCase restoreMovieUseCase,
            UploadMovieImageUseCase uploadMovieImageUseCase
    ) {
        this.createMovieUseCase = createMovieUseCase;
        this.updateMovieUseCase = updateMovieUseCase;
        this.getMovieByIdUseCase = getMovieByIdUseCase;
        this.listMoviesUseCase = listMoviesUseCase;
        this.deleteMovieUseCase = deleteMovieUseCase;
        this.restoreMovieUseCase = restoreMovieUseCase;
        this.uploadMovieImageUseCase = uploadMovieImageUseCase;
    }

    @PostMapping
    public ResponseEntity<MovieResult> create(
            @Valid @RequestBody CreateMovieRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        MovieResult result = createMovieUseCase.execute(new CreateMovieCommand(
                request.title(), request.originalTitle(), request.description(),
                request.releaseYear(), request.duration(), request.director(),
                request.actors(), request.country(), request.language(),
                request.trailerUrl(), request.movieType(), request.totalEpisodes(),
                request.genreIds(), userDetails.getUsername()));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResult> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateMovieRequest request
    ) {
        MovieResult result = updateMovieUseCase.execute(new UpdateMovieCommand(
                id, request.title(), request.originalTitle(), request.description(),
                request.releaseYear(), request.duration(), request.director(),
                request.actors(), request.country(), request.language(),
                request.trailerUrl(), request.movieType(), request.totalEpisodes(),
                request.isFeatured(), request.genreIds()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<MovieDetailResult> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(getMovieByIdUseCase.execute(slug));
    }

    @GetMapping
    public Page<MovieResult> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MovieType type,
            @RequestParam(required = false) VideoStatus status,
            @RequestParam(required = false) String genreId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return listMoviesUseCase.execute(search, type, status, genreId, year, country,
                Math.max(0, page - 1), limit, false);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteMovieUseCase.execute(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<MovieResult> restore(@PathVariable String id) {
        return ResponseEntity.ok(restoreMovieUseCase.execute(id));
    }

    @PostMapping("/{id}/poster")
    public ResponseEntity<MovieResult> uploadPoster(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        MovieResult result = uploadMovieImageUseCase.execute(
                id, "poster", file.getOriginalFilename(),
                file.getInputStream(), file.getSize(), file.getContentType());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/backdrop")
    public ResponseEntity<MovieResult> uploadBackdrop(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        MovieResult result = uploadMovieImageUseCase.execute(
                id, "backdrop", file.getOriginalFilename(),
                file.getInputStream(), file.getSize(), file.getContentType());
        return ResponseEntity.ok(result);
    }
}
