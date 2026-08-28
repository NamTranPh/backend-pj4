package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.domain.model.Episode;

public interface EpisodeRepository {
    Episode save(Episode episode);
    Optional<Episode> findById(String id);
    List<Episode> findAll();
    void deleteById(String id);
    List<Episode> findByMovieId(String movieId);
    Optional<Episode> findByMovieIdAndEpisodeNumber(String movieId, Integer episodeNumber);
    List<Episode> findByMovieIdOrderByEpisodeNumber(String movieId);
    List<Episode> findByStatus(VideoStatus status);
    long countByMovieId(String movieId);
    long countByMovieIdAndStatus(String movieId, VideoStatus status);
}
