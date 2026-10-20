package com.example.backend_pj4.presentation.comment.request;

import jakarta.validation.constraints.NotNull;

/**
 * @param isApproved false la an binh luan. An mot nut la an ca nhanh con cua no, xem
 *                   {@code SetCommentApprovalUseCase}
 */
public record SetCommentApprovalRequest(
        @NotNull
        Boolean isApproved
) {
}
