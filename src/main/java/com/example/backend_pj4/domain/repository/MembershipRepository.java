package com.example.backend_pj4.domain.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.Membership;

import java.util.List;

public interface MembershipRepository {
    // Basic CRUD
    Membership save(Membership membership);
    Optional<Membership> findById(String memberId);
    List<Membership> findAll();
    void deleteById(String memberId);
    
    // User memberships
    List<Membership> findByUserId(String userId);
    Optional<Membership> findActiveByUserId(String userId);
    List<Membership> findByUserIdOrderByEndDateDesc(String userId);
    
    // Plan memberships
    List<Membership> findByPlanId(String planId);
    
    // Status queries
    List<Membership> findByPaymentStatus(String paymentStatus);
    List<Membership> findActiveMemberships();
    List<Membership> findExpiredMemberships();
    List<Membership> findExpiringMemberships(LocalDate date);
    
    // Auto renewal
    List<Membership> findByAutoRenewalTrue();
    List<Membership> findExpiringWithAutoRenewal(LocalDate date);
    
    // Date range queries
    List<Membership> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    List<Membership> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Statistics
    long countByPlanId(String planId);
    long countByPaymentStatus(String paymentStatus);
    long countActiveMemberships();
}