package com.example.backend_pj4.domain.repository.filter;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;

/**
 * Bộ lọc danh sách phim.
 *
 * <p>Gom 7 tham số lọc thành một record thay vì truyền rời: {@code genreId} và {@code country}
 * cùng kiểu {@code String} và đứng cạnh nhau, rất dễ truyền nhầm thứ tự.
 *
 * @param q          từ khoá tìm theo tiêu đề; null hoặc rỗng là không lọc
 * @param publicOnly true thì chỉ lấy phim READY + PUBLIC, và bỏ qua {@code status}
 */
public record MovieFilter(
        String q,
        MovieType movieType,
        VideoStatus status,
        String genreId,
        Integer year,
        String country,
        boolean publicOnly
) {
}
