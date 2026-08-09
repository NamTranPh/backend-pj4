// Bộ xử lý ngoại lệ tập trung toàn cục (Global Exception Handler) cho các Controller.
package com.example.backend_pj4.common.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.response.ApiResponseDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 🔹 CustomException (Ngoại lệ nghiệp vụ - mã lỗi snake_case cùng thông điệp đi kèm)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponseDto<Void> body = ApiResponseDto.error(
                errorCode.name().toLowerCase(),
                errorCode.getMessage()
        );
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

    // 🔹 Validation (Lỗi kiểm tra tính hợp lệ dữ liệu đầu vào DTO @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (var error : ex.getBindingResult().getAllErrors()) {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        }
        ApiResponseDto<Void> body = ApiResponseDto.error(
                ErrorCode.VALIDATION_FAILED.name().toLowerCase(),
                ErrorCode.VALIDATION_FAILED.getMessage(),
                errors
        );
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus()).body(body);
    }

    // 🔹 EntityNotFoundException (Lỗi thực thể JPA không tồn tại)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        ApiResponseDto<Void> body = ApiResponseDto.error(
                ErrorCode.RESOURCE_NOT_FOUND.name().toLowerCase(),
                ex.getMessage() != null ? ex.getMessage() : ErrorCode.RESOURCE_NOT_FOUND.getMessage()
        );
        return ResponseEntity.status(ErrorCode.RESOURCE_NOT_FOUND.getStatus()).body(body);
    }

    // 🔹 Access Denied (Lỗi không đủ quyền hạn truy cập API)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAccessDenied(AccessDeniedException ex) {
        ApiResponseDto<Void> body = ApiResponseDto.error(
                ErrorCode.ACCESS_DENIED.name().toLowerCase(),
                ErrorCode.ACCESS_DENIED.getMessage()
        );
        return ResponseEntity.status(ErrorCode.ACCESS_DENIED.getStatus()).body(body);
    }

    // 🔹 RuntimeException (Các ngoại lệ runtime nghiệp vụ phát sinh)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception:", ex);
        ApiResponseDto<Void> body = ApiResponseDto.error(
                ErrorCode.BAD_REQUEST.name().toLowerCase(),
                ex.getMessage() != null ? ex.getMessage() : ErrorCode.BAD_REQUEST.getMessage()
        );
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getStatus()).body(body);
    }

    // 🔹 Exception (Ngoại lệ hệ thống không xác định)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleUnexpected(Exception ex) {
        log.error("Unexpected error:", ex);
        ApiResponseDto<Void> body = ApiResponseDto.error(
                ErrorCode.INTERNAL_SERVER_ERROR.name().toLowerCase(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(body);
    }
}
