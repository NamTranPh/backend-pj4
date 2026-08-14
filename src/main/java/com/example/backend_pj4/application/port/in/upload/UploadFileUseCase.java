package com.example.backend_pj4.application.port.in.upload;

import com.example.backend_pj4.application.command.upload.UploadFileCommand;
import com.example.backend_pj4.application.dto.upload.UploadResult;

public interface UploadFileUseCase {
    UploadResult execute(UploadFileCommand command);
}
