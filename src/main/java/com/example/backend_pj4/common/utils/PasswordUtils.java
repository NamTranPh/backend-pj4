package com.example.backend_pj4.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Hash mật khẩu
    public static String hashPassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // So sánh mật khẩu nhập vào với mật khẩu đã hash
    public static boolean comparePassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
