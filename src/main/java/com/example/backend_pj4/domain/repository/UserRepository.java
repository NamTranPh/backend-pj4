package com.example.backend_pj4.domain.repository;

import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.filter.UserFilter;

public interface UserRepository {
    User save(User user);
    PageResult<User> find(UserFilter filter, PageQuery query);  /** Danh sách người dùng — lọc, sắp xếp và phân trang theo {@link UserFilter} và {@link PageQuery}. */
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email); /** Tìm kiếm người dùng theo email (không phân biệt hoa/thường). */
    boolean existsByEmail(String email);
    void deleteById(String id);
}
