package com.example.backend_pj4.presentation.comment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.comment.CreateCommentCommand;
import com.example.backend_pj4.application.dto.comment.CommentResult;
import com.example.backend_pj4.application.port.in.comment.CreateCommentUseCase;
import com.example.backend_pj4.application.port.in.comment.DeleteCommentUseCase;
import com.example.backend_pj4.application.port.in.comment.LikeCommentUseCase;
import com.example.backend_pj4.application.port.in.comment.UpdateCommentUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.comment.request.CreateCommentRequest;
import com.example.backend_pj4.presentation.comment.request.UpdateCommentRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Comments")
@AuthRequired
@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CreateCommentUseCase createCommentUseCase;
    private final UpdateCommentUseCase updateCommentUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;
    private final LikeCommentUseCase likeCommentUseCase;

    public CommentController(CreateCommentUseCase createCommentUseCase,
                             UpdateCommentUseCase updateCommentUseCase,
                             DeleteCommentUseCase deleteCommentUseCase,
                             LikeCommentUseCase likeCommentUseCase) {
        this.createCommentUseCase = createCommentUseCase;
        this.updateCommentUseCase = updateCommentUseCase;
        this.deleteCommentUseCase = deleteCommentUseCase;
        this.likeCommentUseCase = likeCommentUseCase;
    }

    @Operation(summary = "Đăng bình luận cho phim, cho một tập, hoặc trả lời một bình luận khác. "
            + "Quyền truy cập: Người dùng đã đăng nhập.")
    @PostMapping("/movies/{movieSlug}/comments")
    public ResponseEntity<CommentResult> create(
            @PathVariable String movieSlug,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthResultUser UserDetails userDetails
    ) {
        CommentResult result = createCommentUseCase.execute(new CreateCommentCommand(
                AppUserDetails.userIdOf(userDetails),
                movieSlug,
                request.episodeId(),
                request.parentId(),
                request.content()));

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Sửa nội dung bình luận của chính mình. "
            + "Quyền truy cập: Chủ sở hữu bình luận.")
    @PatchMapping("/comments/{commentId}")
    public CommentResult update(
            @PathVariable String commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            @AuthResultUser UserDetails userDetails
    ) {
        return updateCommentUseCase.execute(
                commentId, AppUserDetails.userIdOf(userDetails), request.content());
    }

    @Operation(summary = "Xoá bình luận cùng toàn bộ nhánh trả lời bên dưới nó. "
            + "Quyền truy cập: Chủ sở hữu bình luận hoặc ADMIN.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable String commentId,
            @AuthResultUser UserDetails userDetails
    ) {
        deleteCommentUseCase.execute(
                commentId,
                AppUserDetails.userIdOf(userDetails),
                AppUserDetails.roleOf(userDetails) == UserRole.ADMIN);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Thích một bình luận. Quyền truy cập: Người dùng đã đăng nhập.")
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> like(
            @PathVariable String commentId,
            @AuthResultUser UserDetails userDetails
    ) {
        likeCommentUseCase.execute(commentId, AppUserDetails.userIdOf(userDetails), true);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Bỏ thích một bình luận. Quyền truy cập: Người dùng đã đăng nhập.")
    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlike(
            @PathVariable String commentId,
            @AuthResultUser UserDetails userDetails
    ) {
        likeCommentUseCase.execute(commentId, AppUserDetails.userIdOf(userDetails), false);
        return ResponseEntity.ok().build();
    }
}
