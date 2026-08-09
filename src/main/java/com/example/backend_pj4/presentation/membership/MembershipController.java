package com.example.backend_pj4.presentation.membership;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/memberships")
public class MembershipController {

    // Đăng ký gói thành viên
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(
            @RequestParam String userId,
            @RequestParam String planId,
            @RequestParam(defaultValue = "false") boolean autoRenewal) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy gói thành viên đang hoạt động của người dùng
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getActiveMembership(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy toàn bộ gói thành viên của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserMemberships(@PathVariable String userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Lấy danh sách các gói thành viên đang được kích hoạt
    @GetMapping("/plans")
    public ResponseEntity<?> getActivePlans() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Tạo gói thành viên mới
    @PostMapping("/plans")
    public ResponseEntity<?> createPlan(@RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Cập nhật gói thành viên
    @PutMapping("/plans/{planId}")
    public ResponseEntity<?> updatePlan(@PathVariable String planId, @RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Kích hoạt gói thành viên
    @PutMapping("/{memberId}/activate")
    public ResponseEntity<?> activateMembership(@PathVariable String memberId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    // Hủy gói thành viên
    @PutMapping("/{memberId}/cancel")
    public ResponseEntity<?> cancelMembership(@PathVariable String memberId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
