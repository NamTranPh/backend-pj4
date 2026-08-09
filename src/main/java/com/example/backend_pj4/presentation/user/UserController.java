package com.example.backend_pj4.presentation.user;

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
@RequestMapping("/api/v1/users")
public class UserController {

    // Lấy thông tin hồ sơ người dùng hiện tại
    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Cập nhật hồ sơ người dùng hiện tại
    @PatchMapping("/me")
    public ResponseEntity<?> updateMe(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy danh sách tất cả người dùng (Admin)
    @GetMapping
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Tạo người dùng mới (Admin)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy thông tin người dùng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Cập nhật thông tin người dùng theo ID
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Xóa người dùng theo ID (Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
