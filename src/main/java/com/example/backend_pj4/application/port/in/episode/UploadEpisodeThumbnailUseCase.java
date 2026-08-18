package com.example.backend_pj4.application.port.in.episode;

import java.io.InputStream;

import com.example.backend_pj4.application.dto.episode.EpisodeResult;

public interface UploadEpisodeThumbnailUseCase {
    EpisodeResult execute(String movieId, String episodeId, String filename, InputStream data, long size, String contentType);
}
