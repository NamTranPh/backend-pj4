package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.domain.model.User;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    List<User> findAll();
    void deleteById(String id);
    boolean existsById(String id);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmailOrPhone(String email, String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    List<User> findByRole(UserRole role);
    List<User> findByAccountStatus(AccountStatus status);
}
