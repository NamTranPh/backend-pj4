package com.example.backend_pj4.presentation.movie;

import com.example.backend_pj4.common.pagination.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.movie.MovieDetailResult;
import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.port.in.movie.GetMovieByIdUseCase;
import com.example.backend_pj4.application.port.in.movie.ListMoviesQuery;
import com.example.backend_pj4.domain.repository.filter.MovieFilter;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;
import com.example.backend_pj4.common.constants.enums.MovieType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Movies - Public")
@RestController
@RequestMapping("/api/v1/movies")
public class PublicMovieController {

    private final ListMoviesQuery listMoviesQuery;
    private final GetMovieByIdUseCase getMovieByIdUseCase;

    public PublicMovieController(ListMoviesQuery listMoviesQuery, GetMovieByIdUseCase getMovieByIdUseCase) {
        this.listMoviesQuery = listMoviesQuery;
        this.getMovieByIdUseCase = getMovieByIdUseCase;
    }

    @Operation(summary = "Lấy danh sách phim có lọc và phân trang. Quyền truy cập: Public (Công khai).")
    @GetMapping
    public PageResult<MovieResult> list(
            @ModelAttribute PageQueryParamDto params,
            @RequestParam(required = false) MovieType type,
            @RequestParam(required = false) String genreId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String country
    ) {
        return listMoviesQuery.execute(
                new MovieFilter(params.search(), type, null, genreId, year, country, true),
                PageableFactory.create(params, ListMoviesQuery.SORTABLE, ListMoviesQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Lấy thông tin chi tiết phim theo đường dẫn Slug. Quyền truy cập: Public (Công khai).")
    @GetMapping("/{slug}")
    public ResponseEntity<MovieDetailResult> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(getMovieByIdUseCase.execute(slug));
    }
}
