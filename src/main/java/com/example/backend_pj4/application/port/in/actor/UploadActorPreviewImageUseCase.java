package com.example.backend_pj4.application.port.in.actor;

import java.io.InputStream;
import com.example.backend_pj4.application.dto.media.MediaUploadResult;

public interface UploadActorPreviewImageUseCase {
    MediaUploadResult execute(String filename, InputStream data, long size, String contentType);
}

