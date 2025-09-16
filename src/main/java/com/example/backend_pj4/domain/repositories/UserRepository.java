package com.example.backend_pj4.domain.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.domain.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.phone = :phone AND u.isActive = true")
    Optional<User> findActiveUserByPhone(@Param("phone") String phone);

    @Query("SELECT COUNT(u) FROM User u WHERE u.membershipStatus = 'PREMIUM'")
    long countPremiumUsers();

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}