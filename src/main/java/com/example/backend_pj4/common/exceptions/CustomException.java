// Ngoại lệ tùy chỉnh dùng trong ứng dụng, ánh xạ trực tiếp từ ErrorCode.
package com.example.backend_pj4.common.exceptions;

import com.example.backend_pj4.common.constants.ErrorCode;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final int statusCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.name().toLowerCase());
        this.errorCode = errorCode;
        this.statusCode = errorCode.getStatus().value();
    }
}
