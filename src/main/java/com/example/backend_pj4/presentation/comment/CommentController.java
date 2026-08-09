package com.example.backend_pj4.presentation.comment;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    // Tạo bình luận mới trên phim hoặc tập phim
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy danh sách bình luận theo phim
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<?> getCommentsByMovie(@PathVariable String movieId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy danh sách bình luận theo tập phim
    @GetMapping("/episode/{episodeId}")
    public ResponseEntity<?> getCommentsByEpisode(@PathVariable String episodeId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy danh sách bình luận đã được duyệt theo phim
    @GetMapping("/movie/{movieId}/approved")
    public ResponseEntity<?> getApprovedCommentsByMovie(@PathVariable String movieId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy danh sách bình luận đang chờ duyệt
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingComments() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy danh sách trả lời của một bình luận
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<?> getReplies(@PathVariable String commentId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Duyệt bình luận
    @PutMapping("/{commentId}/approve")
    public ResponseEntity<?> approveComment(@PathVariable String commentId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Cập nhật nội dung bình luận
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable String commentId, @RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Xóa bình luận
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Thích một bình luận
    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable String commentId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
