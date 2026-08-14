package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.infrastructure.database.mappers.MembershipPlanPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.MembershipPlanJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaMembershipPlanRepositoryAdapter implements MembershipPlanRepository {

    private final MembershipPlanJpaRepository membershipPlanJpaRepository;
    private final MembershipPlanPersistenceMapper membershipPlanPersistenceMapper;

    @Override
    public MembershipPlan save(MembershipPlan plan) {
        var entity = membershipPlanPersistenceMapper.toEntity(plan);
        var saved = membershipPlanJpaRepository.save(entity);
        return membershipPlanPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<MembershipPlan> findById(String planId) {
        return membershipPlanJpaRepository.findById(planId)
                .map(membershipPlanPersistenceMapper::toDomain);
    }

    @Override
    public List<MembershipPlan> findAll() {
        return membershipPlanJpaRepository.findAll().stream()
                .map(membershipPlanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String planId) {
        membershipPlanJpaRepository.deleteById(planId);
    }

    @Override
    public Optional<MembershipPlan> findByPlanName(String planName) {
        return membershipPlanJpaRepository.findAll().stream()
                .filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(planName))
                .map(membershipPlanPersistenceMapper::toDomain)
                .findFirst();
    }

    @Override
    public List<MembershipPlan> findActivePlans() {
        return membershipPlanJpaRepository.findByIsActiveTrue().stream()
                .map(membershipPlanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MembershipPlan> findInactivePlans() {
        return membershipPlanJpaRepository.findAll().stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsActive()))
                .map(membershipPlanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MembershipPlan> findByPriceRange(Double minPrice, Double maxPrice) {
        return membershipPlanJpaRepository.findAll().stream()
                .filter(p -> p.getPrice() != null 
                        && p.getPrice().doubleValue() >= minPrice 
                        && p.getPrice().doubleValue() <= maxPrice)
                .map(membershipPlanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MembershipPlan> findByDurationDaysLessThanEqual(Integer maxDays) {
        return membershipPlanJpaRepository.findAll().stream()
                .filter(p -> p.getDurationDays() != null && p.getDurationDays() <= maxDays)
                .map(membershipPlanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByPlanName(String planName) {
        return membershipPlanJpaRepository.existsByName(planName);
    }
}


