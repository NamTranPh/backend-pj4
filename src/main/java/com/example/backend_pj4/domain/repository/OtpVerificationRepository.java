package com.example.backend_pj4.domain.repository;

import java.util.Optional;

import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.domain.model.OtpVerification;

public interface OtpVerificationRepository {
    OtpVerification save(OtpVerification otpVerification);
    Optional<OtpVerification> findById(String id);
    Optional<OtpVerification> findByEmailAndType(String email, OtpType type);
    void deleteById(String id);
    void deleteExpired();
}
