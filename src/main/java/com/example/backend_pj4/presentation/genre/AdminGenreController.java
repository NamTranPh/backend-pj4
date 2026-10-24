package com.example.backend_pj4.presentation.genre;

import com.example.backend_pj4.common.pagination.PageResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.genre.CreateGenreCommand;
import com.example.backend_pj4.application.command.genre.UpdateGenreCommand;
import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.application.port.in.genre.CreateGenreUseCase;
import com.example.backend_pj4.application.port.in.genre.DeleteGenreUseCase;
import com.example.backend_pj4.application.port.in.genre.GetGenreByIdUseCase;
import com.example.backend_pj4.application.port.in.genre.ListGenresQuery;
import com.example.backend_pj4.domain.repository.filter.GenreFilter;
import com.example.backend_pj4.presentation.common.request.PageableFactory;
import com.example.backend_pj4.application.port.in.genre.UpdateGenreUseCase;
import com.example.backend_pj4.presentation.genre.request.CreateGenreRequest;
import com.example.backend_pj4.presentation.genre.request.RequestAdminGenreDto;
import com.example.backend_pj4.presentation.genre.request.UpdateGenreRequest;
import org.springdoc.core.annotations.ParameterObject;

import com.example.backend_pj4.common.annotation.AuthRequired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Genres - Admin")
@AuthRequired
@RestController
@RequestMapping("/api/v1/admin/genres")
public class AdminGenreController {

    private final CreateGenreUseCase createGenreUseCase;
    private final UpdateGenreUseCase updateGenreUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final ListGenresQuery listGenresQuery;
    private final DeleteGenreUseCase deleteGenreUseCase;

    public AdminGenreController(
            CreateGenreUseCase createGenreUseCase,
            UpdateGenreUseCase updateGenreUseCase,
            GetGenreByIdUseCase getGenreByIdUseCase,
            ListGenresQuery listGenresQuery,
            DeleteGenreUseCase deleteGenreUseCase
    ) {
        this.createGenreUseCase = createGenreUseCase;
        this.updateGenreUseCase = updateGenreUseCase;
        this.getGenreByIdUseCase = getGenreByIdUseCase;
        this.listGenresQuery = listGenresQuery;
        this.deleteGenreUseCase = deleteGenreUseCase;
    }

    @Operation(summary = "Tạo mới thể loại phim. Quyền truy cập: ADMIN.")
    @PostMapping
    public ResponseEntity<GenreResult> create(@Valid @RequestBody CreateGenreRequest request) {
        GenreResult result = createGenreUseCase.execute(
                new CreateGenreCommand(request.name(), request.icon(), request.description()));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Cập nhật thể loại phim theo ID. Quyền truy cập: ADMIN.")
    @PatchMapping("/{id}")
    public ResponseEntity<GenreResult> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateGenreRequest request
    ) {
        GenreResult result = updateGenreUseCase.execute(
                new UpdateGenreCommand(id, request.name(), request.icon(), request.description(), request.status()));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Lấy chi tiết thể loại phim theo Slug. Quyền truy cập: ADMIN.")
    @GetMapping("/{slug}")
    public ResponseEntity<GenreResult> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(getGenreByIdUseCase.execute(slug));
    }

    @Operation(summary = "Lấy danh sách tất cả thể loại phim cho quản trị viên. Quyền truy cập: ADMIN.")
    @GetMapping
    public PageResult<GenreResult> list(@ParameterObject @ModelAttribute RequestAdminGenreDto params) {
        return listGenresQuery.execute(
                new GenreFilter(params.search(), params.getStatus()),
                PageableFactory.create(params, ListGenresQuery.SORTABLE, ListGenresQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Xóa mềm thể loại phim theo ID. Quyền truy cập: ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteGenreUseCase.execute(id);
        return ResponseEntity.ok().build();
    }
}
