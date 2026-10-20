package com.example.backend_pj4.presentation.rating.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RateMovieRequest(
        @NotNull
        @Min(1)
        @Max(5)
        Integer score,

        // Nếu muốn vừa đánh giá vừa viết đánh giá thì truyền thêm giá trị
        @Size(max = 2000)
        String review
) {
}