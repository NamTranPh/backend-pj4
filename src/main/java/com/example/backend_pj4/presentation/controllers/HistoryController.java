package com.example.backend_pj4.presentation.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.services.history.HistoryService;
import com.example.backend_pj4.common.base.BaseController;
import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.domain.entities.HistoryWatching;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
@Tag(name = "Watch History")
public class HistoryController extends BaseController {

    private final HistoryService historyService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Record watching progress")
    public ApiResponseDto<HistoryWatching> recordWatching(
            @RequestParam String userId,
            @RequestParam String movieId,
            @RequestParam(required = false) String episodeId,
            @RequestParam(required = false) Integer watchDuration,
            @RequestParam(required = false) Integer totalDuration,
            @RequestParam(required = false) Integer lastPosition) {
        HistoryWatching history = historyService.recordWatching(
                userId, movieId, episodeId, watchDuration, totalDuration, lastPosition);
        return ApiResponseDto.success(history, "Watching progress recorded");
    }

    @GetMapping("/user/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get user watch history")
    public ApiResponseDto<List<HistoryWatching>> getUserHistory(@PathVariable String userId) {
        List<HistoryWatching> history = historyService.getUserHistory(userId);
        return ApiResponseDto.success(history, "History retrieved successfully");
    }

    @GetMapping("/user/{userId}/recent")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get recent watch history (top 10)")
    public ApiResponseDto<List<HistoryWatching>> getRecentHistory(@PathVariable String userId) {
        List<HistoryWatching> history = historyService.getTop10History(userId);
        return ApiResponseDto.success(history, "Recent history retrieved");
    }

    @GetMapping("/user/{userId}/movie/{movieId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get watch history for a movie")
    public ApiResponseDto<HistoryWatching> getMovieHistory(
            @PathVariable String userId,
            @PathVariable String movieId) {
        return historyService.getMovieHistory(userId, movieId)
                .map(h -> ApiResponseDto.success(h, "History found"))
                .orElse(ApiResponseDto.success(null, "No history found"));
    }

    @DeleteMapping("/{historyId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Delete watch history entry")
    public ApiResponseDto<Void> deleteHistory(@PathVariable String historyId) {
        historyService.deleteHistory(historyId);
        return ApiResponseDto.success(null, "History deleted successfully");
    }

    @DeleteMapping("/user/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Clear user watch history")
    public ApiResponseDto<Void> clearUserHistory(@PathVariable String userId) {
        historyService.clearUserHistory(userId);
        return ApiResponseDto.success(null, "History cleared successfully");
    }
}
