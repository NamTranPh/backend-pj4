package com.example.backend_pj4.application.services.movie;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.episode.RequestUpdateEpisodeDto;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.entities.Episode;
import com.example.backend_pj4.domain.repository.EpisodeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateEpisodeService extends BaseService {
    private final EpisodeRepository episodeRepository;

    public Episode execute(String episodeId, RequestUpdateEpisodeDto dto) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new RuntimeException("Episode not found"));

        if (dto.getTitle() != null)
            episode.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            episode.setDescription(dto.getDescription());
        if (dto.getDuration() != null)
            episode.setDuration(dto.getDuration());
        if (dto.getVideoUrl() != null)
            episode.setVideoUrl(dto.getVideoUrl());
        if (dto.getThumbnailUrl() != null)
            episode.setThumbnailUrl(dto.getThumbnailUrl());
        if (dto.getAirDate() != null)
            episode.setAirDate(dto.getAirDate());
        if (dto.getIsPremium() != null)
            episode.setIsPremium(dto.getIsPremium());
        if (dto.getIsActive() != null)
            episode.setIsActive(dto.getIsActive());

        return episodeRepository.save(episode);
    }
}