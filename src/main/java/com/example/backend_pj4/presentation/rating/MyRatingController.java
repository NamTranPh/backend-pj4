package com.example.backend_pj4.presentation.rating;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.rating.RateMovieCommand;
import com.example.backend_pj4.application.dto.rating.RatingResult;
import com.example.backend_pj4.application.port.in.rating.DeleteRatingUseCase;
import com.example.backend_pj4.application.port.in.rating.GetMyRatingQuery;
import com.example.backend_pj4.application.port.in.rating.RateMovieUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.rating.request.RateMovieRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Đánh giá của chính người đang đăng nhập.
 *
 * <p>Đặt dưới {@code /api/v1/me/...} chứ không nhét vào {@code /movies/{slug}/ratings}: mọi
 * {@code GET} dưới {@code /api/v1/movies/**} đều là {@code permitAll} theo
 * {@code SecurityConfig}, nên endpoint "đánh giá của tôi" đặt ở đó sẽ nhận principal null.
 */
@Tag(name = "Ratings - Me")
@AuthRequired
@RestController
@RequestMapping("/api/v1/me/ratings")
public class MyRatingController {

    private final RateMovieUseCase rateMovieUseCase;
    private final GetMyRatingQuery getMyRatingQuery;
    private final DeleteRatingUseCase deleteRatingUseCase;

    public MyRatingController(RateMovieUseCase rateMovieUseCase,
                              GetMyRatingQuery getMyRatingQuery,
                              DeleteRatingUseCase deleteRatingUseCase) {
        this.rateMovieUseCase = rateMovieUseCase;
        this.getMyRatingQuery = getMyRatingQuery;
        this.deleteRatingUseCase = deleteRatingUseCase;
    }

    @Operation(summary = "Chấm sao cho phim (1–5). Gọi lại sẽ ghi đè đánh giá cũ. "
            + "Quyền truy cập: Người dùng đã đăng nhập.")
    @PutMapping("/{movieSlug}")
    public RatingResult rate(
            @PathVariable String movieSlug,
            @Valid @RequestBody RateMovieRequest request,
            @AuthResultUser UserDetails userDetails
    ) {
        return rateMovieUseCase.execute(new RateMovieCommand(
                AppUserDetails.userIdOf(userDetails),
                movieSlug,
                request.score(),
                request.review()));
    }

    @Operation(summary = "Xem đánh giá của chính mình cho phim này. "
            + "Quyền truy cập: Người dùng đã đăng nhập.")
    @GetMapping("/{movieSlug}")
    public RatingResult myRating(
            @PathVariable String movieSlug,
            @AuthResultUser UserDetails userDetails
    ) {
        return getMyRatingQuery.execute(AppUserDetails.userIdOf(userDetails), movieSlug);
    }

    @Operation(summary = "Xoá đánh giá của chính mình cho phim này. "
            + "Quyền truy cập: Người dùng đã đăng nhập.")
    @DeleteMapping("/{movieSlug}")
    public ResponseEntity<Void> deleteMyRating(
            @PathVariable String movieSlug,
            @AuthResultUser UserDetails userDetails
    ) {
        deleteRatingUseCase.execute(AppUserDetails.userIdOf(userDetails), movieSlug);
        return ResponseEntity.ok().build();
    }
}
