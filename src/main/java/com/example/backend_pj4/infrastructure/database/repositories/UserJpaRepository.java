package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {
    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);
    Optional<UserJpaEntity> findByPhone(String phone);
    @Query("SELECT u FROM UserJpaEntity u WHERE u.email = :email OR u.phone = :phone")
    Optional<UserJpaEntity> findByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    List<UserJpaEntity> findByRole(UserRole role);
    List<UserJpaEntity> findByAccountStatus(AccountStatus status);
}


