package com.example.backend_pj4.presentation.favorite;

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
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    // Thêm phim vào danh sách yêu thích
    @PostMapping
    public ResponseEntity<?> addToFavorites(@RequestParam String userId, @RequestParam String movieId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Xóa phim khỏi danh sách yêu thích
    @DeleteMapping
    public ResponseEntity<?> removeFromFavorites(@RequestParam String userId, @RequestParam String movieId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy danh sách yêu thích của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserFavorites(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Kiểm tra phim có nằm trong danh sách yêu thích không
    @GetMapping("/user/{userId}/check/{movieId}")
    public ResponseEntity<?> isFavorite(@PathVariable String userId, @PathVariable String movieId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Xóa toàn bộ danh sách yêu thích của người dùng
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> clearUserFavorites(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
