package com.example.backend_pj4.application.port.in.history;

import com.example.backend_pj4.application.command.history.UpdateWatchProgressCommand;

public interface UpdateWatchProgressUseCase {
    void execute(UpdateWatchProgressCommand command);
}
