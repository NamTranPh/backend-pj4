package com.example.backend_pj4.application.services.movie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.entities.Episode;
import com.example.backend_pj4.infrastructure.databases.entities.EpisodeEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetEpisodeService extends BaseService {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Object> execute(String movieId, Integer page, Integer limit) {
        int p = Optional.ofNullable(page).orElse(1);
        int l = Optional.ofNullable(limit).orElse(20);

        // --- BASE QUERY ---
        String jpql = """
            SELECT e FROM EpisodeEntity e 
            WHERE e.movie.movieId = :movieId
            ORDER BY e.episodeNumber ASC
        """;

        TypedQuery<EpisodeEntity> query = entityManager.createQuery(jpql, EpisodeEntity.class);
        query.setParameter("movieId", movieId);
        query.setFirstResult((p - 1) * l);
        query.setMaxResults(l);

        List<EpisodeEntity> episodeEntities = query.getResultList();
        List<Episode> items = episodeEntities.stream().map(this::mapToDomain).toList();

        // --- COUNT QUERY ---
        String countJpql = "SELECT COUNT(e) FROM EpisodeEntity e WHERE e.movie.movieId = :movieId";
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        countQuery.setParameter("movieId", movieId);
        long totalItems = countQuery.getSingleResult();

        Map<String, Object> pagination = Map.of(
                "page", p,
                "limit", l,
                "totalItems", totalItems,
                "totalPages", (int) Math.ceil((double) totalItems / l));

        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("pagination", pagination);
        return result;
    }

    /**
     * 🔹 Lấy chi tiết 1 tập phim theo ID
     */
    public Episode getById(String episodeId) {
        String jpql = """
            SELECT e FROM EpisodeEntity e
            JOIN FETCH e.movie
            WHERE e.episodeId = :episodeId
        """;

        TypedQuery<EpisodeEntity> query = entityManager.createQuery(jpql, EpisodeEntity.class);
        query.setParameter("episodeId", episodeId);

        EpisodeEntity entity = query.getResultStream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Episode not found"));

        return mapToDomain(entity);
    }

    // =======================
    // 🔁 MAP ENTITY → DOMAIN
    // =======================
    private Episode mapToDomain(EpisodeEntity entity) {
        return Episode.builder()
                .episodeId(entity.getEpisodeId())
                .title(entity.getTitle())
                .episodeNumber(entity.getEpisodeNumber())
                .description(entity.getDescription())
                .duration(entity.getDuration())
                .videoUrl(entity.getVideoUrl())
                .thumbnailUrl(entity.getThumbnailUrl())
                .airDate(entity.getAirDate())
                .isPremium(entity.getIsPremium())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
