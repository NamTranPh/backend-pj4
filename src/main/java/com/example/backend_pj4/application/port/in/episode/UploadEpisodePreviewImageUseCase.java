package com.example.backend_pj4.application.port.in.episode;

import java.io.InputStream;
import com.example.backend_pj4.application.dto.media.MediaUploadResult;

public interface UploadEpisodePreviewImageUseCase {
    MediaUploadResult execute(String movieId, String filename, InputStream data, long size, String contentType);
}

