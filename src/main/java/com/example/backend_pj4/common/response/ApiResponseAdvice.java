// Advice toàn cục tự động bọc dữ liệu phản hồi thành công (Success Response Envelope).
package com.example.backend_pj4.common.response;


import org.springframework.core.MethodParameter;
import com.example.backend_pj4.common.pagination.PageResult;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.backend_pj4.common.annotation.IgnoreResponseWrapping;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@RestControllerAdvice(basePackages = "com.example.backend_pj4.presentation")
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public ApiResponseAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Không áp dụng nếu class hoặc method đánh dấu @IgnoreResponseWrapping
        if (returnType.getDeclaringClass().isAnnotationPresent(IgnoreResponseWrapping.class)
                || returnType.hasMethodAnnotation(IgnoreResponseWrapping.class)) {
            return false;
        }
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        // 1. Nếu body đã là ApiResponseDto thì giữ nguyên (không bọc đè)
        if (body instanceof ApiResponseDto) {
            return body;
        }

        // 2. Định nghĩa phản hồi wrap mặc định
        ApiResponseDto<?> wrappedResponse;

        // 3. Dữ liệu trả về là một trang kết quả -> tách items và pagination
        if (body instanceof PageResult<?> result) {
            // page đã đếm từ 1 sẵn trong PageResult, không cộng thêm ở đây
            PaginationDto pagination = PaginationDto.of(
                    result.page(),
                    result.size(),
                    result.totalItems(),
                    result.totalPages());
            wrappedResponse = ApiResponseDto.success(result.items(), pagination);
        } else {
            // Trường hợp dữ liệu thường
            wrappedResponse = ApiResponseDto.success(body);
        }

        // 4. Xử lý đặc biệt nếu Controller trả về kiểu String thô.
        // StringHttpMessageConverter mong đợi kiểu String, nếu trả về Object sẽ bị lỗi ClassCastException.
        // Do đó cần tự serialize sang chuỗi JSON của ApiResponseDto.
        if (body instanceof String
                || !MappingJackson2HttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return objectMapper.writeValueAsString(wrappedResponse);
        }

        return wrappedResponse;
    }
}
