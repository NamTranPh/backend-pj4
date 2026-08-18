package com.example.backend_pj4.application.mapper;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.domain.model.Episode;

public final class EpisodeResultMapper {

    private EpisodeResultMapper() {
    }

    public static EpisodeResult toResult(Episode episode) {
        return new EpisodeResult(
                episode.getId(),
                episode.getMovieId(),
                episode.getEpisodeNumber(),
                episode.getTitle(),
                episode.getDescription(),
                episode.getDuration(),
                episode.getStatus(),
                episode.getThumbnailUrl(),
                episode.getRawFileKey(),
                episode.getMasterPlaylistKey(),
                episode.getAirDate(),
                episode.getViewCount(),
                episode.getCreatedAt()
        );
    }
}
