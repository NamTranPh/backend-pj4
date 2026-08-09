package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend_pj4.infrastructure.database.entities.EmailVerificationJpaEntity;

public interface SpringDataEmailVerificationRepository extends JpaRepository<EmailVerificationJpaEntity, String> {
    Optional<EmailVerificationJpaEntity> findByToken(String token);
    List<EmailVerificationJpaEntity> findByUser_Id(String userId);
    List<EmailVerificationJpaEntity> findByUser_IdAndUsedAtIsNull(String userId);
}


