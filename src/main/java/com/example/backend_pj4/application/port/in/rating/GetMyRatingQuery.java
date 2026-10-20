package com.example.backend_pj4.application.port.in.rating;

import com.example.backend_pj4.application.dto.rating.RatingResult;

/** Cổng ĐỌC đánh giá của chính người đang đăng nhập cho một phim. */
public interface GetMyRatingQuery {
    RatingResult execute(String userId, String movieSlug);
}
