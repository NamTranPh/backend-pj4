package com.example.backend_pj4.presentation.membership;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.membership.MyMembershipResult;
import com.example.backend_pj4.application.port.in.membership.GetMyActiveMembershipQuery;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Membership - User")
@AuthRequired
@RestController
@RequestMapping("/api/v1/me/membership")
public class MyMembershipController {

    private final GetMyActiveMembershipQuery getMyActiveMembershipQuery;

    public MyMembershipController(GetMyActiveMembershipQuery getMyActiveMembershipQuery) {
        this.getMyActiveMembershipQuery = getMyActiveMembershipQuery;
    }

    @Operation(summary = "Lấy thông tin gói thành viên đang hoạt động của tôi. Quyền truy cập: Người dùng đã đăng nhập.")
    @GetMapping
    public ResponseEntity<MyMembershipResult> getMyMembership(@AuthResultUser UserDetails userDetails) {
        String userId = AppUserDetails.userIdOf(userDetails);
        return ResponseEntity.ok(getMyActiveMembershipQuery.execute(userId));
    }
}
