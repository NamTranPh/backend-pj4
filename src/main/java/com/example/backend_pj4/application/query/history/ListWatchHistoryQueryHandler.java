package com.example.backend_pj4.application.query.history;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.history.WatchHistoryResult;
import com.example.backend_pj4.application.port.in.history.ListWatchHistoryQuery;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.HistoryWatching;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.HistoryWatchingRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.filter.HistoryFilter;

@Service
public class ListWatchHistoryQueryHandler implements ListWatchHistoryQuery {

    private final HistoryWatchingRepository historyWatchingRepository;
    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final StorageUrlResolver storageUrlResolver;

    public ListWatchHistoryQueryHandler(HistoryWatchingRepository historyWatchingRepository,
                                        MovieRepository movieRepository,
                                        EpisodeRepository episodeRepository,
                                        StorageUrlResolver storageUrlResolver) {
        this.historyWatchingRepository = historyWatchingRepository;
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.storageUrlResolver = storageUrlResolver;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<WatchHistoryResult> execute(HistoryFilter filter, PageQuery query) {
        PageResult<HistoryWatching> histories = historyWatchingRepository.find(filter, query);
        if (histories.items().isEmpty()) {
            return histories.withItems(List.of());
        }

        Map<String, Movie> moviesById = loadMovies(histories.items());
        Map<String, Episode> episodesById = loadEpisodes(histories.items());

        // withItems thay cho map() vì phải loại dòng trỏ tới phim đã xoá, số phần tử thay đổi
        return histories.withItems(histories.items().stream()
                .map(h -> toResult(h, moviesById, episodesById))
                .filter(Objects::nonNull)
                .toList());
    }

    /** Nạp phim và tập của cả lô trong 2 truy vấn, thay cho 2 truy vấn mỗi dòng lịch sử. */
    private Map<String, Movie> loadMovies(List<HistoryWatching> histories) {
        return movieRepository.findByIds(
                        histories.stream().map(HistoryWatching::getMovieId).filter(Objects::nonNull).toList())
                .stream().collect(Collectors.toMap(Movie::getId, movie -> movie));
    }

    private Map<String, Episode> loadEpisodes(List<HistoryWatching> histories) {
        return episodeRepository.findByIds(
                        histories.stream().map(HistoryWatching::getEpisodeId).filter(Objects::nonNull).toList())
                .stream().collect(Collectors.toMap(Episode::getId, episode -> episode));
    }

    private WatchHistoryResult toResult(HistoryWatching h,
                                        Map<String, Movie> moviesById,
                                        Map<String, Episode> episodesById) {
        Movie movie = moviesById.get(h.getMovieId());
        if (movie == null) {
            return null;
        }

        Episode episode = h.getEpisodeId() != null ? episodesById.get(h.getEpisodeId()) : null;

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
                h.getWatchedAt());
    }
}
