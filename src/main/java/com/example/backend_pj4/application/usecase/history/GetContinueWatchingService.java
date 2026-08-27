package com.example.backend_pj4.application.usecase.history;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.history.WatchHistoryResult;
import com.example.backend_pj4.application.port.in.history.GetContinueWatchingUseCase;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.HistoryWatching;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.HistoryWatchingRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class GetContinueWatchingService implements GetContinueWatchingUseCase {

    private final HistoryWatchingRepository historyWatchingRepository;
    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final StorageUrlResolver storageUrlResolver;

    public GetContinueWatchingService(
            HistoryWatchingRepository historyWatchingRepository,
            MovieRepository movieRepository,
            EpisodeRepository episodeRepository,
            StorageUrlResolver storageUrlResolver
    ) {
        this.historyWatchingRepository = historyWatchingRepository;
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.storageUrlResolver = storageUrlResolver;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WatchHistoryResult> execute(String userId) {
        List<HistoryWatching> histories = historyWatchingRepository.findTop10ByUserId(userId);

        return histories.stream()
                .filter(h -> !Boolean.TRUE.equals(h.getIsCompleted()))
                .map(this::toResult)
                .filter(r -> r != null)
                .toList();
    }

    private WatchHistoryResult toResult(HistoryWatching h) {
        Movie movie = movieRepository.findById(h.getMovieId()).orElse(null);
        if (movie == null) return null;

        Episode episode = h.getEpisodeId() != null
                ? episodeRepository.findById(h.getEpisodeId()).orElse(null)
                : null;

        return new WatchHistoryResult(
                h.getId(),
                movie.getId(),
                movie.getSlug(),
                movie.getTitle(),
                storageUrlResolver.resolvePublicImage(movie.getPosterUrl()),
                h.getEpisodeId(),
                episode != null ? episode.getEpisodeNumber() : null,
                episode != null ? episode.getTitle() : null,
                h.getLastPosition(),
                h.getTotalDuration(),
                h.getProgress(),
                h.getIsCompleted(),
                h.getWatchedAt()
        );
    }
}
