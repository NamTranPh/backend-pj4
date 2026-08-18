package com.example.backend_pj4.presentation.user;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend_pj4.application.command.user.UpdateProfileCommand;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.application.port.in.user.DeleteAvatarUseCase;
import com.example.backend_pj4.application.port.in.user.ListUsersUseCase;
import com.example.backend_pj4.application.port.in.user.GetUserByIdUseCase;
import com.example.backend_pj4.application.port.in.user.ToggleUserBanUseCase;
import com.example.backend_pj4.application.port.in.user.UpdateUserProfileUseCase;
import com.example.backend_pj4.presentation.user.request.ToggleBanRequest;
import com.example.backend_pj4.presentation.user.request.UpdateProfileRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final ToggleUserBanUseCase toggleUserBanUseCase;
    private final DeleteAvatarUseCase deleteAvatarUseCase;

    public UserController(GetCurrentUserUseCase getCurrentUserUseCase,
                           UpdateUserProfileUseCase updateUserProfileUseCase,
                           ListUsersUseCase listUsersUseCase,
                           GetUserByIdUseCase getUserByIdUseCase,
                           ToggleUserBanUseCase toggleUserBanUseCase,
                           DeleteAvatarUseCase deleteAvatarUseCase) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.toggleUserBanUseCase = toggleUserBanUseCase;
        this.deleteAvatarUseCase = deleteAvatarUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResult> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(getCurrentUserUseCase.execute(userDetails.getUsername()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResult> updateMe(@Valid @RequestBody UpdateProfileRequest request,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        String userId = getCurrentUserUseCase.execute(userDetails.getUsername()).id();
        UserProfileResult result = updateUserProfileUseCase.execute(
                new UpdateProfileCommand(userId, request.name(), request.phone(),
                        request.address(), request.profileUrl()));
        return ResponseEntity.ok(result);
    }


    @DeleteMapping("/me/avatar")
    public ResponseEntity<Void> deleteAvatar(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = getCurrentUserUseCase.execute(userDetails.getUsername()).id();
        deleteAvatarUseCase.execute(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResult>> getUsers() {
        return ResponseEntity.ok(listUsersUseCase.execute());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResult> getById(@PathVariable String id) {
        return ResponseEntity.ok(getUserByIdUseCase.execute(id));
    }

    @PatchMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleBan(@PathVariable String id,
                                           @Valid @RequestBody ToggleBanRequest request) {
        toggleUserBanUseCase.execute(id, request.banned());
        return ResponseEntity.ok().build();
    }
}
