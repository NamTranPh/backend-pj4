package com.example.backend_pj4.application.port.in.movie;

import com.example.backend_pj4.application.command.movie.StartUploadCommand;
import com.example.backend_pj4.application.dto.movie.UploadSessionResult;

public interface StartUploadUseCase {
    UploadSessionResult execute(StartUploadCommand command);
}
