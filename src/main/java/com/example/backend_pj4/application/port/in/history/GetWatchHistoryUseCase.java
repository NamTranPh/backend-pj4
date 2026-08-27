package com.example.backend_pj4.application.port.in.history;

import java.util.List;

import com.example.backend_pj4.application.dto.history.WatchHistoryResult;

public interface GetWatchHistoryUseCase {
    List<WatchHistoryResult> execute(String userId);
}
