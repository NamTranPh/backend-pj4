package com.example.backend_pj4.domain.repository.filter;

/** Bộ lọc danh sách người dùng. {@code q} tìm theo email hoặc tên. */
public record UserFilter(String q) {
}
