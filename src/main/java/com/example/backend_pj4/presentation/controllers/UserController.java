package com.example.backend_pj4.presentation.controllers;

import java.util.List;
import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.request.user_cms.RequestCreateUserDto;
import com.example.backend_pj4.application.dto.request.user_cms.RequestGetUserDto;
import com.example.backend_pj4.application.dto.request.user_cms.RequestUpdateUserDto;
import com.example.backend_pj4.application.dto.response.user_cms.ResponseApiArrayUserDto;
import com.example.backend_pj4.application.dto.response.user_cms.ResponseApiUserDto;
import com.example.backend_pj4.application.dto.response.user_cms.UserResponse;
import com.example.backend_pj4.application.services.user.CreateUserService;
import com.example.backend_pj4.application.services.user.DeleteUserService;
import com.example.backend_pj4.application.services.user.GetUserService;
import com.example.backend_pj4.application.services.user.UpdateUserService;
import com.example.backend_pj4.common.base.BaseController;
import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.common.dto.response.PaginationDto;
import com.example.backend_pj4.domain.entities.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management")
@SecurityRequirement(name = "bearerAuth")
public class UserController extends BaseController {

    private final CreateUserService createUserService;
    private final GetUserService getUserService;
    private final UpdateUserService updateUserSseCase;
    private final DeleteUserService deleteUserService;

    @GetMapping
    @Operation(summary = "Get all users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseApiArrayUserDto getAllUsers(@ParameterObject @ModelAttribute RequestGetUserDto dto) {
        var result = getUserService.execute(dto);

        var data = (List<UserResponse>) result.get("data");
        var paginationMap = (Map<String, Object>) result.get("pagination");

        var pagination = PaginationDto.builder()
                .page((int) paginationMap.get("page"))
                .limit((int) paginationMap.get("limit"))
                .totalItems((long) paginationMap.get("totalItems"))
                .totalPages((int) paginationMap.get("totalPages"))
                .build();

        return ResponseApiArrayUserDto.of("Get all genres successfully", data, pagination);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    public ResponseApiUserDto getById(@PathVariable String id) {
        User user = getUserService.executeSingle(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found"));

        return ResponseApiUserDto.of("Get user successfully", UserResponse.fromDomain(user));
    }

    @PostMapping
    @Operation(summary = "Create new user", description = "Create a new user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseApiUserDto create(@RequestBody RequestCreateUserDto dto) {
        User user = createUserService.execute(dto);
        return ResponseApiUserDto.of("User created successfully", UserResponse.fromDomain(user));

    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    public ResponseApiUserDto update(@PathVariable String id, @RequestBody RequestUpdateUserDto dto) {
        User updated = updateUserSseCase.execute(id, dto);
        return ResponseApiUserDto.of("User updated successfully", UserResponse.fromDomain(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponseDto<Void> delete(@PathVariable String id) {
        deleteUserService.execute(id);
        return ApiResponseDto.success(null, "User deleted successfully");
    }
}