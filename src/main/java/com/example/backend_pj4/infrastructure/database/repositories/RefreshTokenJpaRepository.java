package com.example.backend_pj4.infrastructure.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.RefreshTokenJpaEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, String> {

    Optional<RefreshTokenJpaEntity> findByTokenId(String tokenId);

    /**
     * Các phiên đăng nhập còn hiệu lực của một người dùng.
     *
     * <p>Rotation sinh một dòng mới mỗi 15 phút nên một phiên có rất nhiều dòng; chỉ dòng chưa bị
     * thu hồi và chưa hết hạn mới là "đang hoạt động", và mỗi sessionId lúc nào cũng có đúng một
     * dòng như vậy.
     */
    @Query("SELECT r FROM RefreshTokenJpaEntity r WHERE r.userId = :userId AND r.revokedAt IS NULL "
            + "AND r.expiresAt > :now AND r.sessionId IS NOT NULL")
    Page<RefreshTokenJpaEntity> findActiveSessionsByUserId(@Param("userId") String userId,
                                                           @Param("now") LocalDateTime now,
                                                           Pageable pageable);

    /**
     * Phiên này có thuộc về người dùng này và còn hiệu lực không.
     *
     * <p>Trước đây kiểm quyền sở hữu bằng cách nạp toàn bộ phiên rồi duyệt bằng Java
     * (Pageable.unpaged), tức là kéo mọi phiên về chỉ để so một chuỗi.
     */
    @Query("SELECT COUNT(r) > 0 FROM RefreshTokenJpaEntity r WHERE r.userId = :userId "
            + "AND r.sessionId = :sessionId AND r.revokedAt IS NULL AND r.expiresAt > :now")
    boolean existsActiveSession(@Param("userId") String userId,
                                @Param("sessionId") String sessionId,
                                @Param("now") LocalDateTime now);

    // clearAutomatically + flushAutomatically: bulk update đi thẳng DB, bỏ qua persistence
    // context. Không có 2 cờ này thì entity đã nạp trước đó vẫn giữ giá trị cũ.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revokedAt = :now WHERE r.tokenId = :tokenId AND r.revokedAt IS NULL")
    void revokeByTokenId(@Param("tokenId") String tokenId, @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revokedAt = :now WHERE r.userId = :userId AND r.revokedAt IS NULL")
    void revokeAllByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revokedAt = :now WHERE r.userId = :userId AND r.sessionId != :sessionId AND r.revokedAt IS NULL")
    void revokeAllByUserIdExceptSession(@Param("userId") String userId,
                                        @Param("sessionId") String sessionId,
                                        @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE RefreshTokenJpaEntity r SET r.revokedAt = :now WHERE r.sessionId = :sessionId AND r.revokedAt IS NULL")
    void revokeAllBySessionId(@Param("sessionId") String sessionId, @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM RefreshTokenJpaEntity r WHERE r.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);
}
