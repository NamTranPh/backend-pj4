package com.example.backend_pj4.domain.repository.filter;

import com.example.backend_pj4.common.constants.enums.GenreStatus;

/**
 * Bộ lọc danh sách thể loại.
 *
 * <p>Thay cho 4 overload cũ ({@code findAll}, {@code findByStatus},
 * {@code findByNameContainingAndStatus}, {@code findByNameContaining}) — mỗi trường null là
 * không áp điều kiện đó.
 */
public record GenreFilter(String q, GenreStatus status) {
}
