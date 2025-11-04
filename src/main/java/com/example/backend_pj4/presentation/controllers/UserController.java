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

import com.example.backend_pj4.application.dto.request.user_cms.RequestCreateUserCmsDto;
import com.example.backend_pj4.application.dto.request.user_cms.RequestGetUserCmsDto;
import com.example.backend_pj4.application.dto.request.user_cms.RequestUpdateUserCmsDto;
import com.example.backend_pj4.application.dto.response.user_cms.ResponseApiArrayUserCmsDto;
import com.example.backend_pj4.application.dto.response.user_cms.ResponseApiUserCmsDto;
import com.example.backend_pj4.application.dto.response.user_cms.UserCmsResponse;
import com.example.backend_pj4.application.services.user_cms.CreateUserService;
import com.example.backend_pj4.application.services.user_cms.DeleteUserService;
import com.example.backend_pj4.application.services.user_cms.GetUserService;
import com.example.backend_pj4.application.services.user_cms.UpdateUserService;
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
@Tag(name = "User CMS")
@SecurityRequirement(name = "bearerAuth")
public class UserController extends BaseController {

    private final CreateUserService createUserService;
    private final GetUserService getUserService;
    private final UpdateUserService updateUserSseCase;
    private final DeleteUserService deleteUserService;

    @GetMapping
    @Operation(summary = "Get all users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseApiArrayUserCmsDto getUsers(@ParameterObject @ModelAttribute RequestGetUserCmsDto dto) {
        var result = getUserService.execute(dto);

        var data = (List<UserCmsResponse>) result.get("data");
        var paginationMap = (Map<String, Object>) result.get("pagination");

        var pagination = PaginationDto.builder()
                .page((int) paginationMap.get("page"))
                .limit((int) paginationMap.get("limit"))
                .totalItems((long) paginationMap.get("totalItems"))
                .totalPages((int) paginationMap.get("totalPages"))
                .build();

        return ResponseApiArrayUserCmsDto.of("Get all users successfully", data, pagination);
    }

    @PostMapping
    @Operation(summary = "Create new user", description = "Create a new user (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseApiUserCmsDto create(@RequestBody RequestCreateUserCmsDto dto) {
        User user = createUserService.execute(dto);
        return ResponseApiUserCmsDto.of("User created successfully", UserCmsResponse.fromDomain(user));

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    public ResponseApiUserCmsDto getById(@PathVariable String id) {
        User user = getUserService.executeSingle(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return ResponseApiUserCmsDto.of("Get user successfully", UserCmsResponse.fromDomain(user));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#id)")
    public ResponseApiUserCmsDto update(@PathVariable String id, @RequestBody RequestUpdateUserCmsDto dto) {
        User updated = updateUserSseCase.execute(id, dto);
        return ResponseApiUserCmsDto.of("User updated successfully", UserCmsResponse.fromDomain(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponseDto<Void> delete(@PathVariable String id) {
        deleteUserService.execute(id);
        return ApiResponseDto.success(null, "User deleted successfully");
    }
}