package com.example.backend_pj4.presentation.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.api.response.ResponseApiUserListDto;
import com.example.backend_pj4.application.dto.request.user.CreateUserRequest;
import com.example.backend_pj4.application.dto.request.user.UpdateUserRequest;
import com.example.backend_pj4.application.dto.response.UserResponse;
import com.example.backend_pj4.application.services.user.UserService;
import com.example.backend_pj4.common.dto.MetaDto;
import com.example.backend_pj4.common.dto.PaginationDto;
import com.example.backend_pj4.common.dto.RequestPaginationDto;
import com.example.backend_pj4.domain.entities.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve list of all users (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseApiUserListDto> getAllUsers(@Valid RequestPaginationDto pagination) {
        List<User> users = userService.getAllUsers();
        List<UserResponse> response = users.stream()
                .map(UserResponse::fromDomain)
                .toList();

        // tạo response wrapper
        ResponseApiUserListDto apiResponse = new ResponseApiUserListDto();
        int totalItems = users.size();
        int totalPages = (int) Math.ceil((double) totalItems / pagination.getLimit());

        apiResponse.setMeta(new MetaDto(true, "Success", null));
        apiResponse.setData(response);
        apiResponse.setPagination(new PaginationDto(pagination.getPage(), pagination.getLimit(), totalItems, totalPages));

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by user ID")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }

    @PostMapping
    @Operation(summary = "Create new user", description = "Create a new user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = request.toDomain();
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.fromDomain(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        User partial = request.toDomain();
        User updated = userService.updateUser(id, partial);
        return ResponseEntity.ok(UserResponse.fromDomain(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}