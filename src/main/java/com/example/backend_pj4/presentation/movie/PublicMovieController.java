package com.example.backend_pj4.presentation.movie;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.example.backend_pj4.application.dto.movie.MovieDetailResult;
import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.port.in.movie.GetMovieByIdUseCase;
import com.example.backend_pj4.application.port.in.movie.GetRelatedMoviesQuery;
import com.example.backend_pj4.application.port.in.movie.ListMoviesQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.MovieFilter;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.common.request.PageableFactory;
import com.example.backend_pj4.presentation.movie.request.RequestPublicMovieDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Movies - Public")
@RestController
@RequestMapping("/api/v1/movies")
public class PublicMovieController {

    private final ListMoviesQuery listMoviesQuery;
    private final GetMovieByIdUseCase getMovieByIdUseCase;
    private final GetRelatedMoviesQuery getRelatedMoviesQuery;

    public PublicMovieController(ListMoviesQuery listMoviesQuery,
                                 GetMovieByIdUseCase getMovieByIdUseCase,
                                 GetRelatedMoviesQuery getRelatedMoviesQuery) {
        this.listMoviesQuery = listMoviesQuery;
        this.getMovieByIdUseCase = getMovieByIdUseCase;
        this.getRelatedMoviesQuery = getRelatedMoviesQuery;
    }

    @Operation(summary = "Lấy danh sách phim có lọc và phân trang. Quyền truy cập: Public (Công khai).")
    @GetMapping
    public PageResult<MovieResult> list(@ParameterObject @ModelAttribute RequestPublicMovieDto query) {
        return listMoviesQuery.execute(
                new MovieFilter(query.search(), query.getType(), null, query.getGenreId(), query.getYear(), query.getCountry(),
                        query.getAgeRating(), query.getQuality(), query.getAudioTrack(), true),
                PageableFactory.create(query, ListMoviesQuery.SORTABLE, ListMoviesQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Lấy thông tin chi tiết phim theo đường dẫn Slug. Quyền truy cập: Public (Công khai).")
    @GetMapping("/{slug}")
    public ResponseEntity<MovieDetailResult> getBySlug(
            @PathVariable String slug,
            Authentication authentication
    ) {
        String userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            userId = AppUserDetails.userIdOf(userDetails);
        }
        return ResponseEntity.ok(getMovieByIdUseCase.execute(slug, userId));
    }

    @Operation(summary = "Lấy danh sách phim tương tự / liên quan theo slug phim hiện tại. Quyền truy cập: Public (Công khai).")
    @GetMapping("/{slug}/related")
    public ResponseEntity<List<MovieResult>> getRelated(
            @PathVariable String slug,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(getRelatedMoviesQuery.execute(slug, limit));
    }
}
