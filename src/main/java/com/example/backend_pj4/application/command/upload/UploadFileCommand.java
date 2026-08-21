package com.example.backend_pj4.application.command.upload;

import java.io.InputStream;
import com.example.backend_pj4.common.constants.enums.UploadType;
import com.example.backend_pj4.common.constants.enums.UserRole;

public record UploadFileCommand(
        InputStream data,
        long size,
        String contentType,
        String originalFilename,
        UploadType type,
        UserRole userRole,
        String userId
) {}
