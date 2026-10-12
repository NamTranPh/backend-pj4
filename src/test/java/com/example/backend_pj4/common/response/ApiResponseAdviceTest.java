package com.example.backend_pj4.common.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import com.example.backend_pj4.common.pagination.PageResult;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Khoá việc advice nhận ra {@link PageResult} và tách được thông tin phân trang.
 *
 * <p>Đây là chỗ nguy hiểm nhất của đợt đổi sang {@code PageResult}: nếu advice không nhận ra kiểu
 * trả về thì <b>không có lỗi biên dịch nào</b>, body chỉ lặng lẽ mất trường {@code pagination} và
 * FE vỡ.
 */
class ApiResponseAdviceTest {

    private ApiResponseAdvice advice;
    private MethodParameter returnType;

    @BeforeEach
    void setUp() {
        advice = new ApiResponseAdvice(new ObjectMapper());
        returnType = mock(MethodParameter.class);
    }

    private Object wrap(Object body) {
        return advice.beforeBodyWrite(body, returnType, MediaType.APPLICATION_JSON,
                MappingJackson2HttpMessageConverter.class,
                mock(ServerHttpRequest.class), mock(ServerHttpResponse.class));
    }

    @Test
    void pageResult_duocTachThanhDataVaPagination() {
        PageResult<String> page = new PageResult<>(List.of("a", "b"), 35, 2, 10, 4);

        ApiResponseDto<?> body = (ApiResponseDto<?>) wrap(page);

        assertThat(body.getData()).isEqualTo(List.of("a", "b"));
        assertThat(body.getPagination()).isNotNull();
        assertThat(body.getPagination().getPage()).as("page của PageResult đã đếm từ 1").isEqualTo(2);
        assertThat(body.getPagination().getLimit()).isEqualTo(10);
        assertThat(body.getPagination().getTotalItems()).isEqualTo(35);
        assertThat(body.getPagination().getTotalPages()).isEqualTo(4);
    }

    /** DTO thường, không phải String: String đi nhánh tự serialize riêng, không liên quan ở đây. */
    private record SampleDto(String ten) {}

    @Test
    void khongPhaiPageResult_thiKhongCoPagination() {
        SampleDto dto = new SampleDto("xin chao");

        ApiResponseDto<?> body = (ApiResponseDto<?>) wrap(dto);

        assertThat(body.getData()).isEqualTo(dto);
        assertThat(body.getPagination()).isNull();
    }

    @Test
    void trangRong_vanTraPaginationChuKhongPhaiNull() {
        ApiResponseDto<?> body = (ApiResponseDto<?>) wrap(new PageResult<>(List.of(), 0, 1, 15, 0));

        assertThat(body.getPagination()).isNotNull();
        assertThat(body.getPagination().getTotalItems()).isZero();
    }
}
