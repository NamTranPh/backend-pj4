package com.example.backend_pj4.infrastructure.databases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.infrastructure.databases.entities.EpisodeEntity;

@Repository
public interface JpaEpisodeRepository extends JpaRepository<EpisodeEntity, String> {

    @Query("SELECT e FROM EpisodeEntity e WHERE e.movie.movieId = ?1")
    List<EpisodeEntity> findByMovieId(String movieId);

    @Query("SELECT e FROM EpisodeEntity e WHERE e.movie.movieId = ?1 AND e.episodeNumber = ?2")
    Optional<EpisodeEntity> findByMovieIdAndEpisodeNumber(String movieId, Integer episodeNumber);

    @Query("SELECT e FROM EpisodeEntity e WHERE e.movie.movieId = ?1 ORDER BY e.episodeNumber ASC")
    List<EpisodeEntity> findByMovieIdOrderByEpisodeNumber(String movieId);

    @Query("SELECT e FROM EpisodeEntity e WHERE e.movie.movieId = ?1 AND e.isPremium = true")
    List<EpisodeEntity> findPremiumEpisodesByMovieId(String movieId);

    @Query("SELECT e FROM EpisodeEntity e WHERE e.movie.movieId = ?1 AND e.isPremium = false")
    List<EpisodeEntity> findFreeEpisodesByMovieId(String movieId);

    @Query("SELECT e FROM EpisodeEntity e WHERE e.movie.movieId = ?1 AND e.isActive = true")
    List<EpisodeEntity> findActiveEpisodesByMovieId(String movieId);

    @Query("SELECT COUNT(e) FROM EpisodeEntity e WHERE e.movie.movieId = ?1")
    long countByMovieId(String movieId);

    @Query("SELECT COUNT(e) FROM EpisodeEntity e WHERE e.movie.movieId = ?1 AND e.isActive = true")
    long countActiveEpisodesByMovieId(String movieId);

    @Query("SELECT e FROM EpisodeEntity e ORDER BY e.createdAt DESC LIMIT ?1")
    List<EpisodeEntity> findLatestEpisodes(int limit);

    @Query("SELECT e FROM EpisodeEntity e WHERE e.movie.movieId = ?1 AND e.episodeNumber > ?2")
    List<EpisodeEntity> findByMovieIdAndEpisodeNumberGreaterThan(String movieId, Integer episodeNumber);
}
