package com.example.backend_pj4.application.dto.response;

import org.springframework.data.jpa.repository.query.Meta;

import com.example.backend_pj4.application.dto.MetaDto;
import com.example.backend_pj4.application.dto.PaginationDto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private MetaDto meta;
    private AuthData data;
    private PaginationDto pagination;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthData {
        private String phone;
        private String accessToken;
        private String refreshToken;
    }

    // Factory method để tạo response thành công
    public static AuthResponse success(String phone, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .meta(new MetaDto(true, "Success", null))
                .data(new AuthData(phone, accessToken, refreshToken))
                .pagination(new PaginationDto(1, 10, 0, 0))
                .build();
    }
}
