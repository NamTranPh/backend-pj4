package com.example.backend_pj4.application.usecase.history;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.history.DeleteWatchHistoryUseCase;
import com.example.backend_pj4.domain.repository.HistoryWatchingRepository;

@Service
public class DeleteWatchHistoryService implements DeleteWatchHistoryUseCase {

    private final HistoryWatchingRepository historyWatchingRepository;

    public DeleteWatchHistoryService(HistoryWatchingRepository historyWatchingRepository) {
        this.historyWatchingRepository = historyWatchingRepository;
    }

    @Override
    @Transactional
    public void execute(String id, String userId) {
        historyWatchingRepository.deleteByIdAndUserId(id, userId);
    }
}
