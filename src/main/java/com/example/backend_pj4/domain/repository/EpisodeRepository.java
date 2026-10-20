package com.example.backend_pj4.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.repository.filter.EpisodeFilter;

public interface EpisodeRepository {
    Episode save(Episode episode);
    PageResult<Episode> find(EpisodeFilter filter, PageQuery query);
    Optional<Episode> findById(String id);
    List<Episode> findByIds(Collection<String> ids);
    Optional<Episode> findByMovieIdAndEpisodeNumber(String movieId, Integer episodeNumber);
    /** Toàn bộ tập của một phim theo thứ tự, dùng ở trang chi tiết phim. */
    List<Episode> findByMovieIdOrderByEpisodeNumber(String movieId);
    long countByMovieIdAndStatus(String movieId, VideoStatus status);
    void deleteById(String id);
}
