package com.example.backend_pj4.domain.repository;

import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.HistoryWatching;
import com.example.backend_pj4.domain.repository.filter.HistoryFilter;

public interface HistoryWatchingRepository {
    HistoryWatching save(HistoryWatching history);
    PageResult<HistoryWatching> find(HistoryFilter filter, PageQuery query);
    Optional<HistoryWatching> findById(String id);
    /** Tiến độ xem phim lẻ (không có tập), dùng khi cập nhật tiến độ. */
    Optional<HistoryWatching> findByUserIdAndMovieIdAndEpisodeIsNull(String userId, String movieId);
    /** Tiến độ xem một phim, bất kể tập nào, dùng để trả vị trí xem tiếp. */
    Optional<HistoryWatching> findByUserIdAndMovieId(String userId, String movieId);
    /** Tiến độ xem một tập cụ thể. */
    Optional<HistoryWatching> findByUserIdAndMovieIdAndEpisodeId(String userId, String movieId, String episodeId);
    void deleteById(String id);
    void deleteByUserId(String userId);
}
