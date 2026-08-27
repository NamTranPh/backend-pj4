package com.example.backend_pj4.application.port.out;

public interface PlaybackTokenService {

    String issueToken(String contentId, String contentType, String userId);

    PlaybackTokenPayload verifyToken(String token);

    record PlaybackTokenPayload(String contentId, String contentType, String userId) {}
}
