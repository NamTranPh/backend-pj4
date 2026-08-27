package com.example.backend_pj4.presentation.history;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.history.UpdateWatchProgressCommand;
import com.example.backend_pj4.application.dto.history.WatchHistoryResult;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.application.port.in.history.GetContinueWatchingUseCase;
import com.example.backend_pj4.application.port.in.history.GetWatchHistoryUseCase;
import com.example.backend_pj4.application.port.in.history.UpdateWatchProgressUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.presentation.history.request.UpdateWatchProgressRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Watch History")
@AuthRequired
@RestController
@RequestMapping("/api/v1/me/watch-history")
public class WatchHistoryController {

    private final UpdateWatchProgressUseCase updateWatchProgressUseCase;
    private final GetContinueWatchingUseCase getContinueWatchingUseCase;
    private final GetWatchHistoryUseCase getWatchHistoryUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public WatchHistoryController(
            UpdateWatchProgressUseCase updateWatchProgressUseCase,
            GetContinueWatchingUseCase getContinueWatchingUseCase,
            GetWatchHistoryUseCase getWatchHistoryUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase
    ) {
        this.updateWatchProgressUseCase = updateWatchProgressUseCase;
        this.getContinueWatchingUseCase = getContinueWatchingUseCase;
        this.getWatchHistoryUseCase = getWatchHistoryUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @Operation(summary = "Cập nhật tiến độ xem phim.")
    @PostMapping("/progress")
    public ResponseEntity<Void> updateProgress(
            @Valid @RequestBody UpdateWatchProgressRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        updateWatchProgressUseCase.execute(new UpdateWatchProgressCommand(
                userId,
                request.movieId(),
                request.episodeId(),
                request.positionSeconds(),
                request.durationSeconds()
        ));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Danh sách phim đang xem dở.")
    @GetMapping("/continue-watching")
    public List<WatchHistoryResult> continueWatching(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = resolveUserId(userDetails);
        return getContinueWatchingUseCase.execute(userId);
    }

    @Operation(summary = "Lịch sử xem phim.")
    @GetMapping
    public List<WatchHistoryResult> history(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = resolveUserId(userDetails);
        return getWatchHistoryUseCase.execute(userId);
    }

    private String resolveUserId(UserDetails userDetails) {
        UserProfileResult user = getCurrentUserUseCase.execute(userDetails.getUsername());
        return user.id();
    }
}
