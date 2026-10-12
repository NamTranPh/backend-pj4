package com.example.backend_pj4.presentation.user;


import com.example.backend_pj4.common.pagination.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.port.in.user.GetUserByIdUseCase;
import com.example.backend_pj4.application.port.in.user.ListUsersQuery;
import com.example.backend_pj4.domain.repository.filter.UserFilter;
import com.example.backend_pj4.application.port.in.user.ToggleUserBanUseCase;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;
import com.example.backend_pj4.presentation.user.request.ToggleBanRequest;

import com.example.backend_pj4.common.annotation.AuthRequired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Users - Admin")
@AuthRequired
@RestController
@RequestMapping("/api/v1/admin/users")
public class UserController {

    private final ListUsersQuery listUsersQuery;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final ToggleUserBanUseCase toggleUserBanUseCase;

    public UserController(ListUsersQuery listUsersQuery,
                          GetUserByIdUseCase getUserByIdUseCase,
                          ToggleUserBanUseCase toggleUserBanUseCase) {
        this.listUsersQuery = listUsersQuery;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.toggleUserBanUseCase = toggleUserBanUseCase;
    }

    @Operation(summary = "Lấy danh sách người dùng (phân trang). Quyền truy cập: ADMIN.")
    @GetMapping
    public PageResult<UserProfileResult> getUsers(@ModelAttribute PageQueryParamDto params) {
        return listUsersQuery.execute(
                new UserFilter(params.search()),
                PageableFactory.create(params, ListUsersQuery.SORTABLE, ListUsersQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Lấy chi tiết người dùng theo ID. Quyền truy cập: ADMIN.")
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResult> getById(@PathVariable String id) {
        return ResponseEntity.ok(getUserByIdUseCase.execute(id));
    }

    @Operation(summary = "Khóa hoặc mở khóa tài khoản người dùng theo ID. Quyền truy cập: ADMIN.")
    @PatchMapping("/{id}/ban")
    public ResponseEntity<Void> toggleBan(@PathVariable String id,
                                           @Valid @RequestBody ToggleBanRequest request) {
        toggleUserBanUseCase.execute(id, request.banned());
        return ResponseEntity.ok().build();
    }
}
