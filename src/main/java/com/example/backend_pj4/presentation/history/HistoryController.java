package com.example.backend_pj4.presentation.history;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/history")
public class HistoryController {

    // Ghi nhận tiến trình xem phim/tập phim
    @PostMapping
    public ResponseEntity<?> recordWatching(
            @RequestParam String userId,
            @RequestParam String movieId,
            @RequestParam(required = false) String episodeId,
            @RequestParam(required = false) Integer watchDuration,
            @RequestParam(required = false) Integer totalDuration,
            @RequestParam(required = false) Integer lastPosition) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy toàn bộ lịch sử xem của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserHistory(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy lịch sử xem gần đây nhất (top 10)
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<?> getRecentHistory(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy lịch sử xem của người dùng cho một phim cụ thể
    @GetMapping("/user/{userId}/movie/{movieId}")
    public ResponseEntity<?> getMovieHistory(@PathVariable String userId, @PathVariable String movieId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Xóa một bản ghi lịch sử xem
    @DeleteMapping("/{historyId}")
    public ResponseEntity<?> deleteHistory(@PathVariable String historyId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Xóa toàn bộ lịch sử xem của người dùng
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> clearUserHistory(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
