package com.example.backend_pj4.presentation.movie.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record GetPartUrlsRequest(
        @NotEmpty List<Integer> partNumbers
) {
}
