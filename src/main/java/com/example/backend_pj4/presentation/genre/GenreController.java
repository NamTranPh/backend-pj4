package com.example.backend_pj4.presentation.genre;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/genres")
public class GenreController {

    // Lấy danh sách tất cả thể loại
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Tạo thể loại mới
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy thông tin thể loại theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Cập nhật thể loại theo ID
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Xóa thể loại theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
