package com.example.backend_pj4.application.port.in.user;

public interface ToggleUserBanUseCase {
    void execute(String userId, boolean ban);
}
