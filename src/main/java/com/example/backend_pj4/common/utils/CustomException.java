package com.example.backend_pj4.common.utils;

import com.example.backend_pj4.common.constants.ErrorCodes;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final int code;
    private final String message;
    private final int statusCode;

    public CustomException(ErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.statusCode = errorCode.getStatus().value();
    }
}
