package com.example.backend_pj4.presentation.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequest(
        @NotBlank
        @Size(max = 2000)
        String content
) {
}
