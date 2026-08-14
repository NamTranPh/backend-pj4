package com.example.backend_pj4.infrastructure.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.RefreshTokenJpaEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, String> {

    Optional<RefreshTokenJpaEntity> findByTokenId(String tokenId);

    @Query("SELECT r FROM RefreshTokenJpaEntity r WHERE r.userId = :userId AND r.revokedAt IS NULL AND r.expiresAt > :now")
    List<RefreshTokenJpaEntity> findActiveByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revokedAt = :now WHERE r.tokenId = :tokenId AND r.revokedAt IS NULL")
    void revokeByTokenId(@Param("tokenId") String tokenId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revokedAt = :now WHERE r.userId = :userId AND r.revokedAt IS NULL")
    void revokeAllByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.expiresAt < :now")
    void deleteExpired(@Param("now") LocalDateTime now);
}
