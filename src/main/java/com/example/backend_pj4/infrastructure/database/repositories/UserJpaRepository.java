package com.example.backend_pj4.infrastructure.database.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {

    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    /** Tìm theo email hoặc tên; search rỗng thì trả tất cả. */
    @Query("SELECT u FROM UserJpaEntity u WHERE :search IS NULL OR :search = '' "
            + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<UserJpaEntity> searchAll(@Param("search") String search, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE UserJpaEntity u SET u.lastActiveAt = :now WHERE u.email = :email")
    void updateLastActiveAt(@Param("email") String email, @Param("now") LocalDateTime now);
}
