package com.example.backend_pj4.infrastructure.databases.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.infrastructure.databases.entities.MembershipPlanEntity;

@Repository
public interface JpaMembershipPlanRepository extends JpaRepository<MembershipPlanEntity, String> {
    
    List<MembershipPlanEntity> findByIsActiveTrue();
    
    boolean existsByPlanName(String planName);
}
