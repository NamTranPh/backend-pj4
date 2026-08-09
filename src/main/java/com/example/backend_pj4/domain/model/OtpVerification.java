package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.OtpType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class OtpVerification {
    private String id;
    private String email;
    private String codeHash;
    private OtpType type;
    private LocalDateTime expiresAt;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;
}
