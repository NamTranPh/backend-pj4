package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.infrastructure.database.entities.MembershipPlanJpaEntity;

@Repository
public interface MembershipPlanJpaRepository extends JpaRepository<MembershipPlanJpaEntity, String> {
    
    List<MembershipPlanJpaEntity> findByIsActiveTrue();

    boolean existsByName(String name);

    Optional<MembershipPlanJpaEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);
}


