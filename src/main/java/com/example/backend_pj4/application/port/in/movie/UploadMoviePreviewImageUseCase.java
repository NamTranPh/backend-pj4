package com.example.backend_pj4.application.port.in.movie;

import java.io.InputStream;
import com.example.backend_pj4.application.dto.media.MediaUploadResult;

public interface UploadMoviePreviewImageUseCase {
    MediaUploadResult execute(String imageType, String filename, InputStream data, long size, String contentType);
}

