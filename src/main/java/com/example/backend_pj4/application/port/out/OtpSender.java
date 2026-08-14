package com.example.backend_pj4.application.port.out;

import com.example.backend_pj4.common.constants.enums.OtpType;

public interface OtpSender {
    void send(String email, String otpCode, OtpType type);
}
