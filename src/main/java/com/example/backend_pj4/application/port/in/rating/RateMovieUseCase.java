package com.example.backend_pj4.application.port.in.rating;

import com.example.backend_pj4.application.command.rating.RateMovieCommand;
import com.example.backend_pj4.application.dto.rating.RatingResult;

public interface RateMovieUseCase {
    /** Tạo mới hoặc ghi đè đánh giá đã có — mỗi người chỉ có một đánh giá cho một phim. */
    RatingResult execute(RateMovieCommand command);
}
