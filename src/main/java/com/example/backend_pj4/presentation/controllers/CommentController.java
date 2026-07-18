package com.example.backend_pj4.presentation.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.services.comment.CommentService;
import com.example.backend_pj4.common.base.BaseController;
import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.domain.entities.Comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comments")
public class CommentController extends BaseController {

    private final CommentService commentService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Create comment", description = "Create a new comment on a movie or episode")
    public ApiResponseDto<Comment> createComment(
            @RequestParam String userId,
            @RequestParam String movieId,
            @RequestParam(required = false) String episodeId,
            @RequestParam String content,
            @RequestParam(required = false) String parentId) {
        Comment comment = commentService.createComment(userId, movieId, episodeId, content, parentId);
        return ApiResponseDto.success(comment, "Comment created successfully");
    }

    @GetMapping("/movie/{movieId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get comments by movie")
    public ApiResponseDto<List<Comment>> getCommentsByMovie(@PathVariable String movieId) {
        List<Comment> comments = commentService.getCommentsByMovie(movieId);
        return ApiResponseDto.success(comments, "Comments retrieved successfully");
    }

    @GetMapping("/episode/{episodeId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get comments by episode")
    public ApiResponseDto<List<Comment>> getCommentsByEpisode(@PathVariable String episodeId) {
        List<Comment> comments = commentService.getCommentsByEpisode(episodeId);
        return ApiResponseDto.success(comments, "Comments retrieved successfully");
    }

    @GetMapping("/movie/{movieId}/approved")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get approved comments by movie")
    public ApiResponseDto<List<Comment>> getApprovedCommentsByMovie(@PathVariable String movieId) {
        List<Comment> comments = commentService.getApprovedCommentsByMovie(movieId);
        return ApiResponseDto.success(comments, "Comments retrieved successfully");
    }

    @GetMapping("/pending")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending comments")
    public ApiResponseDto<List<Comment>> getPendingComments() {
        List<Comment> comments = commentService.getPendingComments();
        return ApiResponseDto.success(comments, "Pending comments retrieved successfully");
    }

    @GetMapping("/{commentId}/replies")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get replies to a comment")
    public ApiResponseDto<List<Comment>> getReplies(@PathVariable String commentId) {
        List<Comment> replies = commentService.getReplies(commentId);
        return ApiResponseDto.success(replies, "Replies retrieved successfully");
    }

    @PutMapping("/{commentId}/approve")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve comment")
    public ApiResponseDto<Comment> approveComment(@PathVariable String commentId) {
        Comment comment = commentService.approveComment(commentId);
        return ApiResponseDto.success(comment, "Comment approved successfully");
    }

    @PutMapping("/{commentId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Update comment")
    public ApiResponseDto<Comment> updateComment(
            @PathVariable String commentId,
            @RequestParam String content) {
        Comment comment = commentService.updateComment(commentId, content);
        return ApiResponseDto.success(comment, "Comment updated successfully");
    }

    @DeleteMapping("/{commentId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Delete comment")
    public ApiResponseDto<Void> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ApiResponseDto.success(null, "Comment deleted successfully");
    }

    @PostMapping("/{commentId}/like")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Like a comment")
    public ApiResponseDto<Comment> likeComment(@PathVariable String commentId) {
        Comment comment = commentService.likeComment(commentId);
        return ApiResponseDto.success(comment, "Comment liked successfully");
    }
}
