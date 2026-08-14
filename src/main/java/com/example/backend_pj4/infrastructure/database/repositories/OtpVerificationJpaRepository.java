package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.infrastructure.database.entities.OtpVerificationJpaEntity;

public interface OtpVerificationJpaRepository extends JpaRepository<OtpVerificationJpaEntity, String> {
    Optional<OtpVerificationJpaEntity> findFirstByEmailAndTypeOrderByCreatedAtDesc(String email, OtpType type);

    @Modifying
    @Query("DELETE FROM OtpVerificationJpaEntity o WHERE o.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpired();
}


