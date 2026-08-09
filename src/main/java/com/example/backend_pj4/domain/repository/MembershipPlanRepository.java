package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.domain.model.MembershipPlan;

public interface MembershipPlanRepository {
    // Basic CRUD
    MembershipPlan save(MembershipPlan plan);
    Optional<MembershipPlan> findById(String planId);
    List<MembershipPlan> findAll();
    void deleteById(String planId);
    
    // Business queries
    Optional<MembershipPlan> findByPlanName(String planName);
    List<MembershipPlan> findActivePlans();
    List<MembershipPlan> findInactivePlans();
    List<MembershipPlan> findByPriceRange(Double minPrice, Double maxPrice);
    List<MembershipPlan> findByDurationDaysLessThanEqual(Integer maxDays);
    
    // Validation
    boolean existsByPlanName(String planName);
}
