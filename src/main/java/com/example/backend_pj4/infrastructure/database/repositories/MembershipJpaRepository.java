package com.example.backend_pj4.infrastructure.database.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.constants.enums.MembershipPaymentStatus;
import com.example.backend_pj4.infrastructure.database.entities.MembershipJpaEntity;

@Repository
public interface MembershipJpaRepository extends JpaRepository<MembershipJpaEntity, String> {
    
    Optional<MembershipJpaEntity> findByUserId(String userId);
    
    @Query("SELECT m FROM MembershipJpaEntity m WHERE m.user.id = ?1 AND m.isActive = true AND m.endDate >= ?2")
    Optional<MembershipJpaEntity> findActiveMembershipByUserId(String userId, LocalDate currentDate);
    
    List<MembershipJpaEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<MembershipJpaEntity> findByIsActiveTrue();
    
    List<MembershipJpaEntity> findByPaymentStatus(MembershipPaymentStatus paymentStatus);
    
    List<MembershipJpaEntity> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    long countByUserId(String userId);
    
    long countByIsActiveTrue();
    
    long countByPaymentStatus(MembershipPaymentStatus paymentStatus);
    
    @Query("SELECT COUNT(m) FROM MembershipJpaEntity m WHERE m.endDate < ?1 AND m.isActive = true")
    long countExpiredMemberships(LocalDate currentDate);
}

