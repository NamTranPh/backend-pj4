package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.model.EmailVerification;

public interface EmailVerificationRepository {
    EmailVerification save(EmailVerification emailVerification);
    Optional<EmailVerification> findById(String id);
    Optional<EmailVerification> findByToken(String token);
    List<EmailVerification> findByUserId(String userId);
    void deleteById(String id);
}
