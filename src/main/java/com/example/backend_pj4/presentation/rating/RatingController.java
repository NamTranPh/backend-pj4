package com.example.backend_pj4.presentation.rating;

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
@RequestMapping("/api/v1/ratings")
public class RatingController {

    // Đánh giá phim theo điểm số (1-10)
    @PostMapping
    public ResponseEntity<?> rateMovie(
            @RequestParam String userId,
            @RequestParam String movieId,
            @RequestParam Integer score,
            @RequestParam(required = false) String review) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy danh sách đánh giá theo phim
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<?> getRatingsByMovie(@PathVariable String movieId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy điểm đánh giá trung bình của phim
    @GetMapping("/movie/{movieId}/average")
    public ResponseEntity<?> getAverageRating(@PathVariable String movieId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy danh sách đánh giá của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRatings(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy đánh giá của người dùng cho một phim cụ thể
    @GetMapping("/user/{userId}/movie/{movieId}")
    public ResponseEntity<?> getUserMovieRating(@PathVariable String userId, @PathVariable String movieId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy danh sách phim được đánh giá cao nhất
    @GetMapping("/top-rated")
    public ResponseEntity<?> getTopRatedMovies(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Xóa đánh giá của người dùng cho một phim
    @DeleteMapping("/user/{userId}/movie/{movieId}")
    public ResponseEntity<?> deleteUserMovieRating(@PathVariable String userId, @PathVariable String movieId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
