package com.example.backend_pj4.application.port.out;

import java.util.Optional;

import com.example.backend_pj4.domain.model.RefreshToken;

public interface RefreshTokenStore {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByTokenId(String tokenId);
    void revokeByTokenId(String tokenId);
    void revokeAllByUserId(String userId);
}
