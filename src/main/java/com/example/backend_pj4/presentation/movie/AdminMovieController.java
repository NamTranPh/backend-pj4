package com.example.backend_pj4.presentation.movie;

import java.io.IOException;

import com.example.backend_pj4.common.pagination.PageResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springdoc.core.annotations.ParameterObject;

import com.example.backend_pj4.application.command.movie.CreateMovieCommand;
import com.example.backend_pj4.application.command.movie.UpdateMovieCommand;
import com.example.backend_pj4.application.dto.movie.MovieDetailResult;
import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.port.in.movie.CreateMovieUseCase;
import com.example.backend_pj4.application.port.in.movie.DeleteMovieUseCase;
import com.example.backend_pj4.application.port.in.movie.GetMovieByIdUseCase;
import com.example.backend_pj4.application.port.in.movie.ListMoviesQuery;
import com.example.backend_pj4.domain.repository.filter.MovieFilter;
import com.example.backend_pj4.presentation.common.request.PageableFactory;
import com.example.backend_pj4.presentation.movie.request.RequestAdminMovieDto;
import com.example.backend_pj4.application.port.in.movie.PublishMovieUseCase;
import com.example.backend_pj4.application.port.in.movie.RestoreMovieUseCase;
import com.example.backend_pj4.application.port.in.movie.UpdateMovieUseCase;
import com.example.backend_pj4.application.port.in.movie.UploadMovieImageUseCase;
import com.example.backend_pj4.presentation.movie.request.CreateMovieRequest;
import com.example.backend_pj4.presentation.movie.request.UpdateMovieRequest;

import com.example.backend_pj4.common.annotation.AuthRequired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Movies - Admin")
@AuthRequired
@RestController
@RequestMapping("/api/v1/admin/movies")
public class AdminMovieController {

    private final CreateMovieUseCase createMovieUseCase;
    private final UpdateMovieUseCase updateMovieUseCase;
    private final GetMovieByIdUseCase getMovieByIdUseCase;
    private final ListMoviesQuery listMoviesQuery;
    private final DeleteMovieUseCase deleteMovieUseCase;
    private final RestoreMovieUseCase restoreMovieUseCase;
    private final UploadMovieImageUseCase uploadMovieImageUseCase;
    private final PublishMovieUseCase publishMovieUseCase;

    public AdminMovieController(
            CreateMovieUseCase createMovieUseCase,
            UpdateMovieUseCase updateMovieUseCase,
            GetMovieByIdUseCase getMovieByIdUseCase,
            ListMoviesQuery listMoviesQuery,
            DeleteMovieUseCase deleteMovieUseCase,
            RestoreMovieUseCase restoreMovieUseCase,
            UploadMovieImageUseCase uploadMovieImageUseCase,
            PublishMovieUseCase publishMovieUseCase
    ) {
        this.createMovieUseCase = createMovieUseCase;
        this.updateMovieUseCase = updateMovieUseCase;
        this.getMovieByIdUseCase = getMovieByIdUseCase;
        this.listMoviesQuery = listMoviesQuery;
        this.deleteMovieUseCase = deleteMovieUseCase;
        this.restoreMovieUseCase = restoreMovieUseCase;
        this.uploadMovieImageUseCase = uploadMovieImageUseCase;
        this.publishMovieUseCase = publishMovieUseCase;
    }

    @Operation(summary = "Tạo phim mới. Quyền truy cập: ADMIN.")
    @PostMapping
    public ResponseEntity<MovieResult> create(
            @Valid @RequestBody CreateMovieRequest request,
            @AuthResultUser UserDetails userDetails
    ) {
        MovieResult result = createMovieUseCase.execute(new CreateMovieCommand(
                request.title(), request.originalTitle(), request.description(),
                request.releaseYear(), request.duration(), request.director(),
                request.actors(), request.country(), request.language(),
                request.trailerUrl(), request.movieType(), request.totalEpisodes(),
                request.genreIds(), userDetails.getUsername()));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Cập nhật thông tin phim theo ID. Quyền truy cập: ADMIN.")
    @PatchMapping("/{id}")
    public ResponseEntity<MovieResult> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateMovieRequest request
    ) {
        MovieResult result = updateMovieUseCase.execute(new UpdateMovieCommand(
                id, request.title(), request.originalTitle(), request.description(),
                request.releaseYear(), request.duration(), request.director(),
                request.actors(), request.country(), request.language(),
                request.trailerUrl(), request.movieType(), request.totalEpisodes(),
                request.isFeatured(), request.isPremium(), request.status(),
                request.visibility(), request.genreIds()));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Lấy chi tiết phim cho quản trị viên theo Slug. Quyền truy cập: ADMIN.")
    @GetMapping("/{slug}")
    public ResponseEntity<MovieDetailResult> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(getMovieByIdUseCase.execute(slug));
    }

    @Operation(summary = "Lấy danh sách tất cả phim (bao gồm nháp/ẩn) cho quản trị viên. Quyền truy cập: ADMIN.")
    @GetMapping
    public PageResult<MovieResult> list(@ParameterObject @ModelAttribute RequestAdminMovieDto query) {
        return listMoviesQuery.execute(
                new MovieFilter(query.search(), query.getType(), query.getStatus(), query.getGenreId(), query.getYear(), query.getCountry(), false),
                PageableFactory.create(query, ListMoviesQuery.SORTABLE, ListMoviesQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Xóa mềm phim theo ID. Quyền truy cập: ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteMovieUseCase.execute(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Khôi phục phim đã xóa theo ID. Quyền truy cập: ADMIN.")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<MovieResult> restore(@PathVariable String id) {
        return ResponseEntity.ok(restoreMovieUseCase.execute(id));
    }

    @Operation(summary = "Xuất bản phim. Phim lẻ: trigger transcode nếu chưa có. Phim bộ: chuyển READY nếu có tập đã sẵn sàng. Quyền truy cập: ADMIN.")
    @PostMapping("/{id}/publish")
    public ResponseEntity<MovieResult> publish(@PathVariable String id) {
        return ResponseEntity.ok(publishMovieUseCase.execute(id));
    }

    @Operation(summary = "Upload ảnh Poster cho phim. Quyền truy cập: ADMIN.")
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

    @Operation(summary = "Upload ảnh Backdrop / Banner cho phim. Quyền truy cập: ADMIN.")
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
