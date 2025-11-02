package com.example.backend_pj4.application.services.genre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.genre.RequestGetGenreDto;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.common.enums.SortOrder;
import com.example.backend_pj4.domain.entities.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.infrastructure.databases.entities.GenreEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetGenreService extends BaseService {

    private final GenreRepository genreRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Object> execute(RequestGetGenreDto dto) {
        // --- Default values ---
        int page = Optional.ofNullable(dto.getPage()).orElse(1);
        int limit = Optional.ofNullable(dto.getLimit()).orElse(10);
        String sortBy = Optional.ofNullable(dto.getSortBy()).orElse("createdAt");
        SortOrder sortOrder = Optional.ofNullable(dto.getSortOrder()).orElse(SortOrder.desc);
        String q = dto.getQ();

        // --- Base query ---
        StringBuilder queryStr = new StringBuilder("SELECT g FROM GenreEntity g WHERE 1=1");

        // --- SEARCH: q ---
        if (q != null && !q.isEmpty()) {
            queryStr.append(" AND (LOWER(g.name) LIKE LOWER(CONCAT('%', :q, '%'))")
                    .append(" OR LOWER(g.description) LIKE LOWER(CONCAT('%', :q, '%')))");
        }

        // --- SORT ---
        queryStr.append(" ORDER BY g.")
                .append(sortBy)
                .append(" ")
                .append(sortOrder.name()); // <-- dùng enum.name() thay vì toUpperCase()

        // --- MAIN QUERY ---
        TypedQuery<GenreEntity> query = entityManager.createQuery(queryStr.toString(), GenreEntity.class);

        if (q != null && !q.isEmpty()) {
            query.setParameter("q", q);
        }

        // --- PAGINATION ---
        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);

        List<Genre> items = query.getResultList().stream()
                .map(entity -> Genre.builder()
                        .genreId(entity.getGenreId())
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .createdAt(entity.getCreatedAt())
                        .updatedAt(entity.getUpdatedAt())
                        .build())
                .toList();

        // --- COUNT QUERY ---
        String countStr = queryStr.toString()
                .replaceFirst("SELECT g FROM GenreEntity g", "SELECT COUNT(g) FROM GenreEntity g")
                .replaceFirst("ORDER BY.+$", "");

        TypedQuery<Long> countQuery = entityManager.createQuery(countStr, Long.class);

        if (q != null && !q.isEmpty()) {
            countQuery.setParameter("q", q);
        }

        long totalItems = countQuery.getSingleResult();

        // --- RESPONSE ---
        Map<String, Object> pagination = Map.of(
                "page", page,
                "limit", limit,
                "totalItems", totalItems,
                "totalPages", (int) Math.ceil((double) totalItems / limit));

        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("pagination", pagination);

        return result;
    }

    public Optional<Genre> executeSingle(String id) {
        return genreRepository.findById(id);
    }
}
