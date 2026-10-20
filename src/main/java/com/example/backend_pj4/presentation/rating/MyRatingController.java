package com.example.backend_pj4.presentation.rating;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.rating.RateMovieCommand;
import com.example.backend_pj4.application.dto.rating.MyRatingResult;
import com.example.backend_pj4.application.dto.rating.RatingResult;
import com.example.backend_pj4.application.port.in.rating.DeleteRatingUseCase;
import com.example.backend_pj4.application.port.in.rating.GetMyRatingQuery;
import com.example.backend_pj4.application.port.in.rating.ListMyRatingsQuery;
import com.example.backend_pj4.application.port.in.rating.RateMovieUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;
import com.example.backend_pj4.presentation.rating.request.RateMovieRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Ratings - Me")
@AuthRequired
@RestController
@RequestMapping("/api/v1/me/ratings")
public class MyRatingController {

    private final RateMovieUseCase rateMovieUseCase;
    private final GetMyRatingQuery getMyRatingQuery;
    private final DeleteRatingUseCase deleteRatingUseCase;
    private final ListMyRatingsQuery listMyRatingsQuery;

    public MyRatingController(RateMovieUseCase rateMovieUseCase,
                              GetMyRatingQuery getMyRatingQuery,
                              DeleteRatingUseCase deleteRatingUseCase,
                              ListMyRatingsQuery listMyRatingsQuery) {
        this.rateMovieUseCase = rateMovieUseCase;
        this.getMyRatingQuery = getMyRatingQuery;
        this.deleteRatingUseCase = deleteRatingUseCase;
        this.listMyRatingsQuery = listMyRatingsQuery;
    }

    @Operation(summary = "Xem lịch sử các phim chính mình đã đánh giá (phân trang). "
            + "Quyền truy cập: Người dùng đã đăng nhập.")
    @GetMapping
    public PageResult<MyRatingResult> listMyRatings(
            @ModelAttribute PageQueryParamDto params,
            @AuthResultUser UserDetails userDetails
    ) {
        return listMyRatingsQuery.execute(
                AppUserDetails.userIdOf(userDetails),
                PageableFactory.create(params, ListMyRatingsQuery.SORTABLE, ListMyRatingsQuery.DEFAULT_SORT));
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
