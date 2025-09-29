package com.example.backend_pj4.domain.repository;

import java.time.LocalDate;
import java.util.Optional;

import com.example.backend_pj4.domain.entities.Membership;

import java.util.List;

public interface MembershipRepository {
    // Basic CRUD
    Membership save(Membership membership);
    Optional<Membership> findById(Integer memberId);
    List<Membership> findAll();
    void deleteById(Integer memberId);
    
    // User memberships
    List<Membership> findByUserId(Integer userId);
    Optional<Membership> findActiveByUserId(Integer userId);
    List<Membership> findByUserIdOrderByEndDateDesc(Integer userId);
    
    // Plan memberships
    List<Membership> findByPlanId(Integer planId);
    
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
    long countByPlanId(Integer planId);
    long countByPaymentStatus(String paymentStatus);
    long countActiveMemberships();
}