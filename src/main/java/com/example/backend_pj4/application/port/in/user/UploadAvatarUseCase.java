package com.example.backend_pj4.application.port.in.user;

import java.io.InputStream;
import com.example.backend_pj4.application.dto.user.UserProfileResult;

public interface UploadAvatarUseCase {
    UserProfileResult execute(String userId, String filename, InputStream data, long size, String contentType);
}

