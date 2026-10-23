package com.example.backend_pj4.application.usecase.history;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.history.ClearWatchHistoryUseCase;
import com.example.backend_pj4.domain.repository.HistoryWatchingRepository;

@Service
public class ClearWatchHistoryService implements ClearWatchHistoryUseCase {

    private final HistoryWatchingRepository historyWatchingRepository;

    public ClearWatchHistoryService(HistoryWatchingRepository historyWatchingRepository) {
        this.historyWatchingRepository = historyWatchingRepository;
    }

    @Override
    @Transactional
    public void execute(String userId) {
        historyWatchingRepository.deleteByUserId(userId);
    }
}
