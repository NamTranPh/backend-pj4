package com.example.backend_pj4.infrastructure.database.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.UploadSessionJpaEntity;

public interface UploadSessionJpaRepository extends JpaRepository<UploadSessionJpaEntity, String> {

    Optional<UploadSessionJpaEntity> findByTargetIdAndStatus(String targetId, String status);

    @Query("SELECT s FROM UploadSessionJpaEntity s WHERE s.status = 'ACTIVE' AND s.expiresAt < :now")
    List<UploadSessionJpaEntity> findExpired(@Param("now") LocalDateTime now);
}
