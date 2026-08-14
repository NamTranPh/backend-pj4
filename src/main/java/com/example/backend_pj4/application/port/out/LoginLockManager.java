package com.example.backend_pj4.application.port.out;

public interface LoginLockManager {
    boolean isLocked(String email);
    void recordFailure(String email);
    void resetFailures(String email);
}
