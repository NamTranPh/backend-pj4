package com.example.backend_pj4.application.dto.response.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAuthDto {
    // private MetaDto meta;
    private AuthData data;
    // private PaginationDto pagination;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthData {
        private String phone;
        private String accessToken;
        private String refreshToken;
    }

    // Factory method để tạo response thành công
    public static ResponseAuthDto success(String phone, String accessToken, String refreshToken) {
        return ResponseAuthDto.builder()
                // .meta(new MetaDto(true, "Success", null))
                .data(new AuthData(phone, accessToken, refreshToken))
                // .pagination(new PaginationDto(1, 10, 0, 0))
                .build();
    }
}
