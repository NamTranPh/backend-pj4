package com.example.backend_pj4.presentation.rating;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.rating.RatingResult;
import com.example.backend_pj4.application.dto.rating.RatingSummaryResult;
import com.example.backend_pj4.application.port.in.rating.GetRatingSummaryQuery;
import com.example.backend_pj4.application.port.in.rating.ListRatingsQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Ratings - Public")
@RestController
@RequestMapping("/api/v1/movies/{movieSlug}/ratings")
public class PublicRatingController {

    private final ListRatingsQuery listRatingsQuery;
    private final GetRatingSummaryQuery getRatingSummaryQuery;

    public PublicRatingController(ListRatingsQuery listRatingsQuery,
                                  GetRatingSummaryQuery getRatingSummaryQuery) {
        this.listRatingsQuery = listRatingsQuery;
        this.getRatingSummaryQuery = getRatingSummaryQuery;
    }

    @Operation(summary = "Danh sách đánh giá kèm nhận xét của phim. Quyền truy cập: Public (Công khai).")
    @GetMapping
    public PageResult<RatingResult> list(
            @PathVariable String movieSlug,
            @ModelAttribute PageQueryParamDto params
    ) {
        return listRatingsQuery.execute(
                movieSlug,
                PageableFactory.create(params,
                        ListRatingsQuery.SORTABLE, ListRatingsQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Điểm trung bình (1–5 sao) và số lượt đánh giá của phim. "
            + "Quyền truy cập: Public (Công khai).")
    @GetMapping("/summary")
    public RatingSummaryResult summary(@PathVariable String movieSlug) {
        return getRatingSummaryQuery.execute(movieSlug);
    }
}
