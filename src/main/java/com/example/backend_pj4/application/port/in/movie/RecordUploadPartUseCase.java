package com.example.backend_pj4.application.port.in.movie;

import com.example.backend_pj4.application.command.movie.RecordUploadPartCommand;

public interface RecordUploadPartUseCase {
    void execute(RecordUploadPartCommand command);
}
