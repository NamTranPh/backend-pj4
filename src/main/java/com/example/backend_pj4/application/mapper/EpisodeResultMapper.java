package com.example.backend_pj4.application.mapper;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.domain.model.Episode;

@Component
public class EpisodeResultMapper {

    private final StorageUrlResolver storageUrlResolver;

    public EpisodeResultMapper(StorageUrlResolver storageUrlResolver) {
        this.storageUrlResolver = storageUrlResolver;
    }

    public EpisodeResult toResult(Episode episode) {
        return new EpisodeResult(
                episode.getId(),
                episode.getMovieId(),
                episode.getEpisodeNumber(),
                episode.getTitle(),
                episode.getDescription(),
                episode.getDuration(),
                episode.getStatus(),
                storageUrlResolver.resolvePublicImage(episode.getThumbnailUrl()),
                episode.getRawFileKey(),
                episode.getMasterPlaylistKey(),
                episode.getAirDate(),
                episode.getViewCount(),
                episode.getCreatedAt()
        );
    }
}
