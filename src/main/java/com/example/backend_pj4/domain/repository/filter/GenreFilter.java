package com.example.backend_pj4.domain.repository.filter;

import com.example.backend_pj4.common.constants.enums.GenreStatus;
public record GenreFilter(String q, GenreStatus status) {
}
