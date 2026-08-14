package com.example.backend_pj4.presentation.upload.request;

import org.springframework.web.multipart.MultipartFile;
import com.example.backend_pj4.common.constants.enums.UploadType;
import jakarta.validation.constraints.NotNull;

public record UploadRequest(
        @NotNull(message = "File is required") MultipartFile file,
        @NotNull(message = "Upload type is required") UploadType type
) {}
