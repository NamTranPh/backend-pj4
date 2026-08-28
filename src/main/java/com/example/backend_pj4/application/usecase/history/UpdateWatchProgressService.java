package com.example.backend_pj4.application.usecase.history;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.history.UpdateWatchProgressCommand;
import com.example.backend_pj4.application.port.in.history.UpdateWatchProgressUseCase;
import com.example.backend_pj4.domain.model.HistoryWatching;
import com.example.backend_pj4.domain.repository.HistoryWatchingRepository;

@Service
public class UpdateWatchProgressService implements UpdateWatchProgressUseCase {

    private final HistoryWatchingRepository historyWatchingRepository;

    public UpdateWatchProgressService(HistoryWatchingRepository historyWatchingRepository) {
        this.historyWatchingRepository = historyWatchingRepository;
    }

    @Override
    @Transactional
    public void execute(UpdateWatchProgressCommand command) {
        var existing = (command.episodeId() == null || command.episodeId().isBlank())
                ? historyWatchingRepository.findByUserIdAndMovieIdAndEpisodeIsNull(command.userId(), command.movieId())
                : historyWatchingRepository.findByUserIdAndMovieIdAndEpisodeId(command.userId(), command.movieId(), command.episodeId());

        BigDecimal progress = BigDecimal.ZERO;
        if (command.durationSeconds() != null && command.durationSeconds() > 0) {
            progress = BigDecimal.valueOf(command.positionSeconds())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(command.durationSeconds()), 2, RoundingMode.HALF_UP);
        }

        boolean isCompleted = command.durationSeconds() != null
                && command.durationSeconds() > 0
                && (command.durationSeconds() - command.positionSeconds()) <= 30;

        if (existing.isPresent()) {
            HistoryWatching updated = existing.get().toBuilder()
                    .watchDuration(command.positionSeconds())
                    .totalDuration(command.durationSeconds())
                    .lastPosition(command.positionSeconds())
                    .progress(progress)
                    .isCompleted(isCompleted)
                    .watchedAt(LocalDateTime.now())
                    .build();
            historyWatchingRepository.save(updated);
        } else {
            HistoryWatching newHistory = HistoryWatching.builder()
                    .userId(command.userId())
                    .movieId(command.movieId())
                    .episodeId(command.episodeId())
                    .watchDuration(command.positionSeconds())
                    .totalDuration(command.durationSeconds())
                    .lastPosition(command.positionSeconds())
                    .progress(progress)
                    .isCompleted(isCompleted)
                    .watchedAt(LocalDateTime.now())
                    .build();
            historyWatchingRepository.save(newHistory);
        }
    }
}
