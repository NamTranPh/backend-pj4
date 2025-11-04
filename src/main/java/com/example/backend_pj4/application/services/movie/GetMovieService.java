package com.example.backend_pj4.application.services.movie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.movie.RequestGetMovieCmsDto;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.common.enums.SortOrder;
import com.example.backend_pj4.domain.entities.Episode;
import com.example.backend_pj4.domain.entities.Genre;
import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.infrastructure.databases.entities.EpisodeEntity;
import com.example.backend_pj4.infrastructure.databases.entities.GenreEntity;
import com.example.backend_pj4.infrastructure.databases.entities.MovieEntity;
import com.example.backend_pj4.infrastructure.databases.entities.UserEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetMovieService extends BaseService {

    private final MovieRepository movieRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Lấy danh sách phim có tìm kiếm, phân trang và sắp xếp
     */
    public Map<String, Object> execute(RequestGetMovieCmsDto dto) {
        int page = Optional.ofNullable(dto.getPage()).orElse(1);
        int limit = Optional.ofNullable(dto.getLimit()).orElse(10);
        String sortBy = Optional.ofNullable(dto.getSortBy()).orElse("createdAt");
        SortOrder sortOrder = Optional.ofNullable(dto.getSortOrder()).orElse(SortOrder.desc);
        String q = dto.getQ();

        // --- BASE QUERY ---
        StringBuilder queryStr = new StringBuilder("SELECT m FROM MovieEntity m WHERE 1=1");

        // --- SEARCH (title, director, actors, description)
        if (q != null && !q.isEmpty()) {
            queryStr.append(" AND (LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%'))")
                    .append(" OR LOWER(m.director) LIKE LOWER(CONCAT('%', :q, '%'))")
                    .append(" OR LOWER(m.actors) LIKE LOWER(CONCAT('%', :q, '%'))")
                    .append(" OR LOWER(m.description) LIKE LOWER(CONCAT('%', :q, '%')))");
        }

        // --- SORT ---
        queryStr.append(" ORDER BY m.")
                .append(sortBy)
                .append(" ")
                .append(sortOrder.name());

        // --- MAIN QUERY ---
        TypedQuery<MovieEntity> query = entityManager.createQuery(queryStr.toString(), MovieEntity.class);
        if (q != null && !q.isEmpty()) {
            query.setParameter("q", q);
        }

        query.setFirstResult((page - 1) * limit);
        query.setMaxResults(limit);

        List<MovieEntity> movieEntities = query.getResultList();

        // --- MAP ENTITY → DOMAIN ---
        List<Movie> items = movieEntities.stream().map(this::mapToDomain).toList();

        // --- COUNT QUERY ---
        String countStr = queryStr.toString()
                .replaceFirst("SELECT m FROM MovieEntity m", "SELECT COUNT(m) FROM MovieEntity m")
                .replaceFirst("ORDER BY.+$", "");

        TypedQuery<Long> countQuery = entityManager.createQuery(countStr, Long.class);
        if (q != null && !q.isEmpty()) {
            countQuery.setParameter("q", q);
        }

        long totalItems = countQuery.getSingleResult();

        // --- PAGINATION INFO ---
        Map<String, Object> pagination = Map.of(
                "page", page,
                "limit", limit,
                "totalItems", totalItems,
                "totalPages", (int) Math.ceil((double) totalItems / limit));

        // --- RESPONSE ---
        Map<String, Object> result = new HashMap<>();
        result.put("data", items);
        result.put("pagination", pagination);

        return result;
    }

    /**
     * Lấy chi tiết 1 phim
     */
    public Optional<Movie> executeSingle(String id) {
        return movieRepository.findById(id);
    }

    // =======================
    // 🔁 MAP ENTITY → DOMAIN
    // =======================
    private Movie mapToDomain(MovieEntity entity) {
        return Movie.builder()
                .movieId(entity.getMovieId())
                .title(entity.getTitle())
                .originalTitle(entity.getOriginalTitle())
                .description(entity.getDescription())
                .releaseYear(entity.getReleaseYear())
                .duration(entity.getDuration())
                .director(entity.getDirector())
                .actors(entity.getActors())
                .country(entity.getCountry())
                .language(entity.getLanguage())
                .trailerUrl(entity.getTrailerUrl())
                .posterUrl(entity.getPosterUrl())
                .backdropUrl(entity.getBackdropUrl())
                .rating(entity.getRating())
                .viewCount(entity.getViewCount())
                .movieType(entity.getMovieType())
                .totalEpisodes(entity.getTotalEpisodes())
                .status(entity.getStatus())
                .isPremium(entity.getIsPremium())
                .isFeatured(entity.getIsFeatured())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(mapUser(entity.getCreatedBy()))
                .genres(mapGenres(entity.getGenres()))
                .episodes(mapEpisodes(entity.getEpisodes()))
                .build();
    }

    private User mapUser(UserEntity user) {
        if (user == null)
            return null;
        return User.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

    private List<Genre> mapGenres(List<GenreEntity> genres) {
        if (genres == null)
            return List.of();
        return genres.stream()
                .map(g -> Genre.builder()
                        .genreId(g.getGenreId())
                        .name(g.getName())
                        .description(g.getDescription())
                        .createdAt(g.getCreatedAt())
                        .updatedAt(g.getUpdatedAt())
                        .build())
                .toList();
    }

    private List<Episode> mapEpisodes(List<EpisodeEntity> episodes) {
        if (episodes == null)
            return List.of();
        return episodes.stream()
                .map(e -> Episode.builder()
                        .episodeId(e.getEpisodeId())
                        .title(e.getTitle())
                        .episodeNumber(e.getEpisodeNumber())
                        .videoUrl(e.getVideoUrl())
                        .createdAt(e.getCreatedAt())
                        .build())
                .toList();
    }
}
