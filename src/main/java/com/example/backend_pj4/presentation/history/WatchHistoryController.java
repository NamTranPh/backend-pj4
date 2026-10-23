package com.example.backend_pj4.presentation.history;


import org.springframework.http.ResponseEntity;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.history.UpdateWatchProgressCommand;
import com.example.backend_pj4.application.dto.history.WatchHistoryResult;
import com.example.backend_pj4.application.port.in.history.ClearWatchHistoryUseCase;
import com.example.backend_pj4.application.port.in.history.DeleteWatchHistoryUseCase;
import com.example.backend_pj4.application.port.in.history.ListWatchHistoryQuery;
import com.example.backend_pj4.domain.repository.filter.HistoryFilter;
import com.example.backend_pj4.application.port.in.history.UpdateWatchProgressUseCase;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

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
    private final ListWatchHistoryQuery listWatchHistoryQuery;
    private final DeleteWatchHistoryUseCase deleteWatchHistoryUseCase;
    private final ClearWatchHistoryUseCase clearWatchHistoryUseCase;

    public WatchHistoryController(
            UpdateWatchProgressUseCase updateWatchProgressUseCase,
            ListWatchHistoryQuery listWatchHistoryQuery,
            DeleteWatchHistoryUseCase deleteWatchHistoryUseCase,
            ClearWatchHistoryUseCase clearWatchHistoryUseCase
    ) {
        this.updateWatchProgressUseCase = updateWatchProgressUseCase;
        this.listWatchHistoryQuery = listWatchHistoryQuery;
        this.deleteWatchHistoryUseCase = deleteWatchHistoryUseCase;
        this.clearWatchHistoryUseCase = clearWatchHistoryUseCase;
    }

    @Operation(summary = "Cập nhật tiến độ xem phim.")
    @PostMapping("/progress")
    public ResponseEntity<Void> updateProgress(
            @Valid @RequestBody UpdateWatchProgressRequest request,
            @AuthResultUser UserDetails userDetails
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
    public PageResult<WatchHistoryResult> continueWatching(
            @AuthResultUser UserDetails userDetails,
            @ModelAttribute PageQueryParamDto params) {
        // unfinishedOnly = true: chỉ khác lịch sử xem ở đúng cờ này
        return listWatchHistoryQuery.execute(
                new HistoryFilter(resolveUserId(userDetails), null, true),
                PageableFactory.create(params,
                        ListWatchHistoryQuery.SORTABLE, ListWatchHistoryQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Lịch sử xem phim.")
    @GetMapping
    public PageResult<WatchHistoryResult> history(
            @AuthResultUser UserDetails userDetails,
            @ModelAttribute PageQueryParamDto params) {
        return listWatchHistoryQuery.execute(
                new HistoryFilter(resolveUserId(userDetails), params.search(), false),
                PageableFactory.create(params,
                        ListWatchHistoryQuery.SORTABLE, ListWatchHistoryQuery.DEFAULT_SORT));
    }

    @Operation(summary = "Xoá 1 bản ghi lịch sử xem phim.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable String id,
            @AuthResultUser UserDetails userDetails
    ) {
        deleteWatchHistoryUseCase.execute(id, resolveUserId(userDetails));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Xoá toàn bộ lịch sử xem phim của tài khoản.")
    @DeleteMapping
    public ResponseEntity<Void> clearAll(
            @AuthResultUser UserDetails userDetails
    ) {
        clearWatchHistoryUseCase.execute(resolveUserId(userDetails));
        return ResponseEntity.ok().build();
    }

    /** userId lấy thẳng từ principal — filter đã nạp sẵn, không cần tra lại database. */
    private String resolveUserId(UserDetails userDetails) {
        return AppUserDetails.userIdOf(userDetails);
    }
}
