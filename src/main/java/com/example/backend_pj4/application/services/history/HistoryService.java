package com.example.backend_pj4.application.services.history;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.entities.Episode;
import com.example.backend_pj4.domain.entities.HistoryWatching;
import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.HistoryRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoryService extends BaseService {

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;

    public HistoryWatching recordWatching(String userId, String movieId, String episodeId,
            Integer watchDuration, Integer totalDuration, Integer lastPosition) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        Episode episode = null;
        if (episodeId != null && !episodeId.isEmpty()) {
            episode = episodeRepository.findById(episodeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
        }

        Optional<HistoryWatching> existingHistory;
        if (episode != null) {
            existingHistory = historyRepository.findByUserAndMovieAndEpisode(userId, movieId, episodeId);
        } else {
            existingHistory = historyRepository.findByUserAndMovie(userId, movieId);
        }

        HistoryWatching history;
        if (existingHistory.isPresent()) {
            history = existingHistory.get();
            history.setWatchDuration(watchDuration != null ? watchDuration : history.getWatchDuration());
            history.setTotalDuration(totalDuration != null ? totalDuration : history.getTotalDuration());
            history.setLastPosition(lastPosition != null ? lastPosition : history.getLastPosition());
        } else {
            history = HistoryWatching.builder()
                    .watchDuration(watchDuration != null ? watchDuration : 0)
                    .totalDuration(totalDuration != null ? totalDuration : 0)
                    .lastPosition(lastPosition != null ? lastPosition : 0)
                    .progress(BigDecimal.ZERO)
                    .isCompleted(false)
                    .build();
            history.setUser(user);
            history.setMovie(movie);
            history.setEpisode(episode);
        }

        if (totalDuration != null && totalDuration > 0) {
            BigDecimal progress = BigDecimal.valueOf(watchDuration != null ? watchDuration : 0)
                    .divide(BigDecimal.valueOf(totalDuration), 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            history.setProgress(progress);
            history.setIsCompleted(progress.compareTo(BigDecimal.valueOf(90)) >= 0);
        }

        return historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<HistoryWatching> getUserHistory(String userId) {
        return historyRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<HistoryWatching> getTop10History(String userId) {
        return historyRepository.findTop10ByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<HistoryWatching> getMovieHistory(String userId, String movieId) {
        return historyRepository.findByUserAndMovie(userId, movieId);
    }

    public void deleteHistory(String historyId) {
        if (!historyRepository.existsById(historyId)) {
            throw new ResourceNotFoundException("History not found");
        }
        historyRepository.deleteById(historyId);
    }

    public void clearUserHistory(String userId) {
        historyRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long countUserHistory(String userId) {
        return historyRepository.countByUserId(userId);
    }
}
