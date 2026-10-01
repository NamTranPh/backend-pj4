// Port giới hạn số lần thử sai OTP, chống brute-force mã 6 số trong thời gian OTP còn hiệu lực.
package com.example.backend_pj4.application.port.out;

import com.example.backend_pj4.common.constants.enums.OtpType;

public interface OtpAttemptLimiter {

    /**
     * Tăng bộ đếm số lần thử OTP của (email, type).
     * Ném CustomException(OTP_MAX_ATTEMPTS_EXCEEDED) khi vượt ngưỡng cấu hình.
     * Phải gọi TRƯỚC khi so sánh hash OTP.
     */
    void checkAndIncrement(String email, OtpType type);

    /** Xoá bộ đếm khi OTP được nhập đúng. */
    void reset(String email, OtpType type);
}
