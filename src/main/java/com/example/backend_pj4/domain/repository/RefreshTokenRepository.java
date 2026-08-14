package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.model.RefreshToken;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByTokenId(String tokenId);
    List<RefreshToken> findActiveByUserId(String userId);
    void revokeByTokenId(String tokenId);
    void revokeAllByUserId(String userId);
    void deleteExpired();
}
