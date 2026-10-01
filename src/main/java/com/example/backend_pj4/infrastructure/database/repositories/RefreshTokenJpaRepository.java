package com.example.backend_pj4.infrastructure.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.RefreshTokenJpaEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, String> {

    Optional<RefreshTokenJpaEntity> findByTokenId(String tokenId);

    // clearAutomatically + flushAutomatically: bulk update đi thẳng DB, bỏ qua persistence
    // context. Không có 2 cờ này thì entity đã nạp trước đó vẫn giữ giá trị cũ.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revokedAt = :now WHERE r.tokenId = :tokenId AND r.revokedAt IS NULL")
    void revokeByTokenId(@Param("tokenId") String tokenId, @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revokedAt = :now WHERE r.userId = :userId AND r.revokedAt IS NULL")
    void revokeAllByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revokedAt = :now WHERE r.sessionId = :sessionId AND r.revokedAt IS NULL")
    void revokeAllBySessionId(@Param("sessionId") String sessionId, @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);
}
