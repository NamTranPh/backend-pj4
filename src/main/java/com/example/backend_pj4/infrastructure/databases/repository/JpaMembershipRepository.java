package com.example.backend_pj4.infrastructure.databases.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.enums.PaymentStatus;
import com.example.backend_pj4.infrastructure.databases.entities.MembershipEntity;

@Repository
public interface JpaMembershipRepository extends JpaRepository<MembershipEntity, String> {
    
    Optional<MembershipEntity> findByUserUserId(String userId);
    
    @Query("SELECT m FROM MembershipEntity m WHERE m.user.userId = ?1 AND m.isActive = true AND m.endDate >= ?2")
    Optional<MembershipEntity> findActiveMembershipByUserId(String userId, LocalDate currentDate);
    
    List<MembershipEntity> findByUserUserIdOrderByCreatedAtDesc(String userId);
    
    List<MembershipEntity> findByIsActiveTrue();
    
    List<MembershipEntity> findByPaymentStatus(PaymentStatus paymentStatus);
    
    List<MembershipEntity> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    long countByUserUserId(String userId);
    
    long countByIsActiveTrue();
    
    long countByPaymentStatus(PaymentStatus paymentStatus);
    
    @Query("SELECT COUNT(m) FROM MembershipEntity m WHERE m.endDate < ?1 AND m.isActive = true")
    long countExpiredMemberships(LocalDate currentDate);
}
