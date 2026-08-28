package com.example.backend_pj4.presentation.movie;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.movie.MovieDetailResult;
import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.port.in.movie.GetMovieByIdUseCase;
import com.example.backend_pj4.application.port.in.movie.ListMoviesUseCase;
import com.example.backend_pj4.common.constants.enums.MovieType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Movies - Public")
@RestController
@RequestMapping("/api/v1/movies")
public class PublicMovieController {

    private final ListMoviesUseCase listMoviesUseCase;
    private final GetMovieByIdUseCase getMovieByIdUseCase;

    public PublicMovieController(ListMoviesUseCase listMoviesUseCase, GetMovieByIdUseCase getMovieByIdUseCase) {
        this.listMoviesUseCase = listMoviesUseCase;
        this.getMovieByIdUseCase = getMovieByIdUseCase;
    }

    @Operation(summary = "Lấy danh sách phim có lọc và phân trang. Quyền truy cập: Public (Công khai).")
    @GetMapping
    public Page<MovieResult> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MovieType type,
            @RequestParam(required = false) String genreId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String country,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return listMoviesUseCase.execute(search, type, null, genreId, year, country,
                Math.max(0, page - 1), limit, true);
    }

    @Operation(summary = "Lấy thông tin chi tiết phim theo đường dẫn Slug. Quyền truy cập: Public (Công khai).")
    @GetMapping("/{slug}")
    public ResponseEntity<MovieDetailResult> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(getMovieByIdUseCase.execute(slug));
    }
}
