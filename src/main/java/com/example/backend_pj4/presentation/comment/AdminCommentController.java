package com.example.backend_pj4.presentation.comment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springdoc.core.annotations.ParameterObject;

import com.example.backend_pj4.application.dto.comment.AdminCommentResult;
import com.example.backend_pj4.application.port.in.comment.ListCommentsAdminQuery;
import com.example.backend_pj4.application.port.in.comment.SetCommentApprovalUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.presentation.comment.request.RequestAdminCommentDto;
import com.example.backend_pj4.presentation.comment.request.SetCommentApprovalRequest;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Comments - Admin")
@AuthRequired
@RestController
@RequestMapping("/api/v1/admin/comments")
public class AdminCommentController {

    private final ListCommentsAdminQuery listCommentsAdminQuery;
    private final SetCommentApprovalUseCase setCommentApprovalUseCase;

    public AdminCommentController(ListCommentsAdminQuery listCommentsAdminQuery,
                                  SetCommentApprovalUseCase setCommentApprovalUseCase) {
        this.listCommentsAdminQuery = listCommentsAdminQuery;
        this.setCommentApprovalUseCase = setCommentApprovalUseCase;
    }

    @Operation(summary = "Danh sách bình luận để kiểm duyệt — phẳng, gồm cả bình luận đang bị ẩn. "
            + "Quyền truy cập: ADMIN.")
    @GetMapping
    public PageResult<AdminCommentResult> list(@ParameterObject @ModelAttribute RequestAdminCommentDto query) {
        return listCommentsAdminQuery.execute(
                query.getMovieId(),
                query.getIsApproved(),
                query.search(),
                PageableFactory.create(query,
                        ListCommentsAdminQuery.SORTABLE, ListCommentsAdminQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Ẩn hoặc hiện lại một bình luận. Ẩn một bình luận là ẩn luôn cả nhánh "
            + "trả lời bên dưới nó. Quyền truy cập: ADMIN.")
    @PatchMapping("/{commentId}/approval")
    public ResponseEntity<Void> setApproval(
            @PathVariable String commentId,
            @Valid @RequestBody SetCommentApprovalRequest request
    ) {
        setCommentApprovalUseCase.execute(commentId, request.isApproved());
        return ResponseEntity.ok().build();
    }
}
