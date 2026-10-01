package com.example.backend_pj4.infrastructure.database.adapters;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.domain.model.RefreshToken;
import com.example.backend_pj4.domain.repository.RefreshTokenRepository;
import com.example.backend_pj4.infrastructure.database.mappers.RefreshTokenPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.RefreshTokenJpaRepository;

@Repository
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository springDataRepo;
    private final RefreshTokenPersistenceMapper mapper;

    public JpaRefreshTokenRepositoryAdapter(RefreshTokenJpaRepository springDataRepo,
                                            RefreshTokenPersistenceMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return mapper.toDomain(springDataRepo.save(mapper.toEntity(refreshToken)));
    }

    @Override
    public Optional<RefreshToken> findByTokenId(String tokenId) {
        return springDataRepo.findByTokenId(tokenId).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void revokeByTokenId(String tokenId) {
        springDataRepo.revokeByTokenId(tokenId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void revokeAllByUserId(String userId) {
        springDataRepo.revokeAllByUserId(userId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void revokeAllBySessionId(String sessionId) {
        springDataRepo.revokeAllBySessionId(sessionId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public int deleteExpired() {
        return springDataRepo.deleteExpired(LocalDateTime.now());
    }
}
