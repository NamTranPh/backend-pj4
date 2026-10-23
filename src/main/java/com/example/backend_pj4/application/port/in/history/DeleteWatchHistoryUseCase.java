package com.example.backend_pj4.application.port.in.history;

public interface DeleteWatchHistoryUseCase {
    void execute(String id, String userId);
}
