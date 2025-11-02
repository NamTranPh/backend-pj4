// package com.example.backend_pj4.application.exceptions;

// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.validation.FieldError;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// import com.example.backend_pj4.common.dto.response.ApiResponseDto;

// import jakarta.persistence.EntityNotFoundException;
// import lombok.extern.slf4j.Slf4j;

// @RestControllerAdvice
// @Slf4j
// public class GlobalExceptionHandler {
    
//     @ExceptionHandler(ResourceNotFoundException.class)
//     public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
//         ErrorResponse error = ErrorResponse.builder()
//                 .timestamp(LocalDateTime.now())
//                 .status(HttpStatus.NOT_FOUND.value())
//                 .error("Resource Not Found")
//                 .message(ex.getMessage())
//                 .build();
        
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
//     }
    
//     @ExceptionHandler(BadCredentialsException.class)
//     public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
//         ErrorResponse error = ErrorResponse.builder()
//                 .timestamp(LocalDateTime.now())
//                 .status(HttpStatus.UNAUTHORIZED.value())
//                 .error("Authentication Failed")
//                 .message("Invalid email or password")
//                 .build();
        
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
//     }
    
//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
//         Map<String, String> errors = new HashMap<>();
//         ex.getBindingResult().getAllErrors().forEach((error) -> {
//             String fieldName = ((FieldError) error).getField();
//             String errorMessage = error.getDefaultMessage();
//             errors.put(fieldName, errorMessage);
//         });
        
//         ErrorResponse error = ErrorResponse.builder()
//                 .timestamp(LocalDateTime.now())
//                 .status(HttpStatus.BAD_REQUEST.value())
//                 .error("Validation Failed")
//                 .message("Invalid input data")
//                 .validationErrors(errors)
//                 .build();
        
//         return ResponseEntity.badRequest().body(error);
//     }
    
//     @ExceptionHandler(RuntimeException.class)
//     public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
//         log.error("Runtime exception occurred", ex);
        
//         ErrorResponse error = ErrorResponse.builder()
//                 .timestamp(LocalDateTime.now())
//                 .status(HttpStatus.BAD_REQUEST.value())
//                 .error("Bad Request")
//                 .message(ex.getMessage())
//                 .build();
        
//         return ResponseEntity.badRequest().body(error);
//     }
    
//     @ExceptionHandler(Exception.class)
//     public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
//         log.error("Unexpected error occurred", ex);
        
//         ErrorResponse error = ErrorResponse.builder()
//                 .timestamp(LocalDateTime.now())
//                 .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                 .error("Internal Server Error")
//                 .message("An unexpected error occurred")
//                 .build();
        
//         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//     }

//      @ExceptionHandler(EntityNotFoundException.class)
//     @ResponseStatus(HttpStatus.NOT_FOUND)
//     public ApiResponseDto<?> handleNotFound(EntityNotFoundException e) {
//         return ApiResponseDto.error(e.getMessage());
//     }

//     @ExceptionHandler(Exception.class)
//     @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//     public ApiResponseDto<?> handleGeneral(Exception e) {
//         return ApiResponseDto.error("Internal Server Error: " + e.getMessage());
//     }
    
//     @lombok.Data
//     @lombok.Builder
//     public static class ErrorResponse {
//         private LocalDateTime timestamp;
//         private int status;
//         private String error;
//         private String message;
//         private Map<String, String> validationErrors;
//     }
// }

package com.example.backend_pj4.application.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend_pj4.common.dto.response.ApiResponseDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 🔹 Resource Not Found (Custom exception)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage(),
                null
        );
    }

    // 🔹 Bad Credentials (Authentication)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication Failed",
                "Invalid email or password",
                null
        );
    }

    // 🔹 Validation (DTO @Valid lỗi)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (var error : ex.getBindingResult().getAllErrors()) {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        }

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Invalid input data",
                errors
        );
    }

    // 🔹 EntityNotFoundException (JPA entity không tồn tại)
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponseDto<?> handleEntityNotFound(EntityNotFoundException ex) {
        return ApiResponseDto.error(ex.getMessage());
    }

    // 🔹 RuntimeException (ngoại lệ business, ví dụ IllegalArgumentException, NullPointer...)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception:", ex);
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                null
        );
    }

    // 🔹 Exception (fallback — bắt tất cả lỗi không xác định)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error:", ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred",
                null
        );
    }

    // 🔸 Helper method tạo ErrorResponse
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            Map<String, String> validationErrors
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    // 🔹 Inner DTO cho lỗi chuẩn (Swagger hiển thị đẹp)
    @lombok.Data
    @lombok.Builder
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private Map<String, String> validationErrors;
    }
}
