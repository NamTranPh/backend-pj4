package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.infrastructure.databases.entities.Episode;

public interface EpisodeRepository {
    // Basic CRUD
    Episode save(Episode episode);

    Optional<Episode> findById(String episodeId);

    List<Episode> findAll();

    void deleteById(String episodeId);

    // Movie-specific queries
    List<Episode> findByMovieId(String movieId);

    Optional<Episode> findByMovieIdAndEpisodeNumber(String movieId, Integer episodeNumber);

    List<Episode> findByMovieIdOrderByEpisodeNumber(String movieId);

    // Premium episodes
    List<Episode> findPremiumEpisodesByMovieId(String movieId);

    List<Episode> findFreeEpisodesByMovieId(String movieId);

    // Active episodes
    List<Episode> findActiveEpisodesByMovieId(String movieId);

    // Statistics
    long countByMovieId(String movieId);

    long countActiveEpisodesByMovieId(String movieId);

    // Latest episodes
    List<Episode> findLatestEpisodes(int limit);

    List<Episode> findByMovieIdAndEpisodeNumberGreaterThan(String movieId, Integer episodeNumber);
}
