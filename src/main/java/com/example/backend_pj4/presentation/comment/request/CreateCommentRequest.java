package com.example.backend_pj4.presentation.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @param episodeId binh luan ve mot tap cu the; bo trong la binh luan cap phim. Bi bo qua khi co
 *                  {@code parentId}: binh luan con luon ke thua pham vi cua binh luan cha
 * @param parentId  binh luan dang duoc tra loi; bo trong la tao binh luan goc
 */
public record CreateCommentRequest(
        @NotBlank
        @Size(max = 2000)
        String content,

        String episodeId,

        String parentId
) {
}
