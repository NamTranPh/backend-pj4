package com.example.backend_pj4.application.port.out;

public interface PasswordHasher {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
