package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.infrastructure.database.entities.EpisodeJpaEntity;

public interface EpisodeJpaRepository extends JpaRepository<EpisodeJpaEntity, String> {

    /** Tập của một phim, tìm theo tiêu đề. Search rỗng thì trả tất cả. */
    @Query("SELECT e FROM EpisodeJpaEntity e WHERE e.movie.id = :movieId "
            + "AND (:search IS NULL OR :search = '' "
            + "     OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<EpisodeJpaEntity> searchByMovieId(@Param("movieId") String movieId,
                                           @Param("search") String search,
                                           Pageable pageable);
    List<EpisodeJpaEntity> findByMovie_Id(String movieId);
    Optional<EpisodeJpaEntity> findByMovie_IdAndEpisodeNumber(String movieId, Integer episodeNumber);
    List<EpisodeJpaEntity> findByMovie_IdOrderByEpisodeNumberAsc(String movieId);
    List<EpisodeJpaEntity> findByStatus(VideoStatus status);
    long countByMovie_Id(String movieId);
    long countByMovie_IdAndStatus(String movieId, VideoStatus status);
}


