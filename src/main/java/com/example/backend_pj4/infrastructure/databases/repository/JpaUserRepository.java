package com.example.backend_pj4.infrastructure.databases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.infrastructure.databases.entities.UserEntity;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByPhone(String phone);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :identifier OR u.phone = :identifier")
    Optional<UserEntity> findByEmailOrPhone(String identifier);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    // membership status
    List<UserEntity> findByMembershipStatus(String membershipStatus);

    // membership expired
    @Query("SELECT u FROM UserEntity u WHERE u.membershipExpiryDate < CURRENT_DATE")
    List<UserEntity> findExpiredMemberships();

    // role
    @Query("SELECT u FROM UserEntity u WHERE u.role.roleId = :roleId")
    List<UserEntity> findByRole(String roleId);

    List<UserEntity> findByIsActiveTrue();

    List<UserEntity> findByIsActiveFalse();
}