package com.example.backend_pj4.infrastructure.database.mappers;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.infrastructure.database.entities.MovieJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class MoviePersistenceMapper {

    private final GenrePersistenceMapper genrePersistenceMapper;
    private final EpisodePersistenceMapper episodePersistenceMapper;
    private final UserPersistenceMapper userPersistenceMapper;
    private final ActorPersistenceMapper actorPersistenceMapper;

    public MoviePersistenceMapper(GenrePersistenceMapper genrePersistenceMapper,
                                  EpisodePersistenceMapper episodePersistenceMapper,
                                  UserPersistenceMapper userPersistenceMapper,
                                  ActorPersistenceMapper actorPersistenceMapper) {
        this.genrePersistenceMapper = genrePersistenceMapper;
        this.episodePersistenceMapper = episodePersistenceMapper;
        this.userPersistenceMapper = userPersistenceMapper;
        this.actorPersistenceMapper = actorPersistenceMapper;
    }

    /**
     * Bản đầy đủ, dùng cho trang chi tiết phim.
     *
     * <p>Chạm vào {@code getEpisodes()} và {@code getCreatedBy()} — hai quan hệ LAZY, nên mỗi
     * lần gọi là thêm truy vấn. Chấp nhận được khi lấy một phim, <b>không</b> dùng cho danh sách:
     * xem {@link #toListDomain(MovieJpaEntity)}.
     */
    public Movie toDomain(MovieJpaEntity entity) {
        if (entity == null) return null;
        return baseBuilder(entity)
                .rawFileKey(entity.getRawFileKey())
                .masterPlaylistKey(entity.getMasterPlaylistKey())
                .resolutions(entity.getResolutions())
                .createdBy(userPersistenceMapper.toSimpleDomain(entity.getCreatedBy()))
                .genres(mapGenres(entity))
                .movieActors(entity.getMovieActors() != null
                        ? entity.getMovieActors().stream().map(actorPersistenceMapper::toDomain).toList()
                        : Collections.emptyList())
                .episodes(entity.getEpisodes() != null
                        ? entity.getEpisodes().stream().map(episodePersistenceMapper::toSimpleDomain).toList()
                        : Collections.emptyList())
                .build();
    }

    /**
     * Bản rút gọn cho DANH SÁCH phim.
     *
     * <p>Khác {@link #toDomain} ở chỗ <b>không chạm</b> {@code getEpisodes()} và
     * {@code getCreatedBy()}. Hai quan hệ đó là LAZY, mà {@code MovieResult} (DTO trả cho client)
     * không có trường nào dùng tới chúng — nạp về rồi vứt đi. Với một trang 15 phim, bỏ hai lần
     * chạm đó là bớt 30 câu truy vấn; phim bộ 50 tập còn bớt thêm hàng trăm dòng đọc vô ích.
     *
     * <p>{@code genres} vẫn phải nạp vì {@code MovieResult.genres} có dùng — nhưng
     * {@code MovieJpaEntity.genres} đã có {@code @BatchSize} nên cả trang gộp thành một truy vấn.
     *
     * <p>Số tập hiển thị lấy từ cột {@code total_episodes}, không phải đếm collection.
     */
    public Movie toListDomain(MovieJpaEntity entity) {
        if (entity == null) return null;
        return baseBuilder(entity)
                .genres(mapGenres(entity))
                .movieActors(entity.getMovieActors() != null
                        ? entity.getMovieActors().stream().map(actorPersistenceMapper::toDomain).toList()
                        : Collections.emptyList())
                .episodes(Collections.emptyList())
                .build();
    }

    /** Phần chung của hai bản map, chỉ gồm cột của chính bảng movie. */
    private Movie.MovieBuilder baseBuilder(MovieJpaEntity entity) {
        return Movie.builder()
                .id(entity.getId())
                .slug(entity.getSlug())
                .title(entity.getTitle())
                .originalTitle(entity.getOriginalTitle())
                .description(entity.getDescription())
                .releaseYear(entity.getReleaseYear())
                .duration(entity.getDuration())
                .director(entity.getDirector())
                .country(entity.getCountry())
                .language(entity.getLanguage())
                .trailerUrl(entity.getTrailerUrl())
                .posterUrl(entity.getPosterUrl())
                .backdropUrl(entity.getBackdropUrl())
                .movieType(entity.getMovieType())
                .totalEpisodes(entity.getTotalEpisodes())
                .status(entity.getStatus())
                .visibility(entity.getVisibility())
                .isPremium(entity.getIsPremium())
                .isFeatured(entity.getIsFeatured())
                .ageRating(entity.getAgeRating())
                .videoQualities(entity.getVideoQualities())
                .audioTracks(entity.getAudioTracks())
                .rating(entity.getRating())
                .viewCount(entity.getViewCount())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());
    }

    private List<Genre> mapGenres(MovieJpaEntity entity) {
        return entity.getGenres() != null
                ? entity.getGenres().stream().map(genrePersistenceMapper::toDomain).toList()
                : Collections.emptyList();
    }

    public MovieJpaEntity toEntity(Movie domain) {
        if (domain == null) return null;
        MovieJpaEntity entity = new MovieJpaEntity();
        entity.setId(domain.getId());
        entity.setSlug(domain.getSlug());
        entity.setTitle(domain.getTitle());
        entity.setOriginalTitle(domain.getOriginalTitle());
        entity.setDescription(domain.getDescription());
        entity.setReleaseYear(domain.getReleaseYear());
        entity.setDuration(domain.getDuration());
        entity.setDirector(domain.getDirector());
        entity.setCountry(domain.getCountry());
        entity.setLanguage(domain.getLanguage());
        entity.setTrailerUrl(domain.getTrailerUrl());
        entity.setPosterUrl(domain.getPosterUrl());
        entity.setBackdropUrl(domain.getBackdropUrl());
        entity.setMovieType(domain.getMovieType());
        entity.setTotalEpisodes(domain.getTotalEpisodes());
        entity.setStatus(domain.getStatus());
        entity.setVisibility(domain.getVisibility());
        entity.setIsPremium(domain.getIsPremium());
        entity.setIsFeatured(domain.getIsFeatured());
        if (domain.getAgeRating() != null) {
            entity.setAgeRating(domain.getAgeRating());
        }
        if (domain.getVideoQualities() != null && !domain.getVideoQualities().isEmpty()) {
            entity.setVideoQualities(domain.getVideoQualities());
        }
        if (domain.getAudioTracks() != null && !domain.getAudioTracks().isEmpty()) {
            entity.setAudioTracks(domain.getAudioTracks());
        }
        entity.setRating(domain.getRating());
        entity.setViewCount(domain.getViewCount());
        entity.setRawFileKey(domain.getRawFileKey());
        entity.setMasterPlaylistKey(domain.getMasterPlaylistKey());
        entity.setResolutions(domain.getResolutions());
        entity.setDeletedAt(domain.getDeletedAt());
        if (domain.getCreatedBy() != null && domain.getCreatedBy().getId() != null) {
            UserJpaEntity user = new UserJpaEntity();
            user.setId(domain.getCreatedBy().getId());
            entity.setCreatedBy(user);
        }
        if (domain.getGenres() != null) {
            entity.setGenres(domain.getGenres().stream().map(genrePersistenceMapper::toEntity).toList());
        }
        if (domain.getMovieActors() != null) {
            List<com.example.backend_pj4.infrastructure.database.entities.MovieActorJpaEntity> actorEntities =
                    domain.getMovieActors().stream().map(ma -> {
                        com.example.backend_pj4.infrastructure.database.entities.MovieActorJpaEntity mae = actorPersistenceMapper.toEntity(ma);
                        mae.setMovie(entity);
                        return mae;
                    }).toList();
            if (entity.getMovieActors() == null) {
                entity.setMovieActors(new java.util.ArrayList<>());
            } else {
                entity.getMovieActors().clear();
            }
            entity.getMovieActors().addAll(actorEntities);
        }
        return entity;
    }

    public Movie toSimpleDomain(MovieJpaEntity entity) {
        if (entity == null) return null;
        return Movie.builder()
                .id(entity.getId())
                .slug(entity.getSlug())
                .title(entity.getTitle())
                .posterUrl(entity.getPosterUrl())
                .movieType(entity.getMovieType())
                .status(entity.getStatus())
                .rating(entity.getRating())
                .viewCount(entity.getViewCount())
                .build();
    }
}
