package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.infrastructure.database.mappers.MembershipPlanPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.SpringDataMembershipPlanRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaMembershipPlanRepositoryAdapter implements MembershipPlanRepository {

    private final SpringDataMembershipPlanRepository SpringDataMembershipPlanRepository;
    private final MembershipPlanPersistenceMapper MembershipPlanPersistenceMapper;

    @Override
    public MembershipPlan save(MembershipPlan plan) {
        var entity = MembershipPlanPersistenceMapper.toEntity(plan);
        var saved = SpringDataMembershipPlanRepository.save(entity);
        return MembershipPlanPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<MembershipPlan> findById(String planId) {
        return SpringDataMembershipPlanRepository.findById(planId)
                .map(MembershipPlanPersistenceMapper::toDomain);
    }

    @Override
    public List<MembershipPlan> findAll() {
        return SpringDataMembershipPlanRepository.findAll().stream()
                .map(MembershipPlanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String planId) {
        SpringDataMembershipPlanRepository.deleteById(planId);
    }

    @Override
    public Optional<MembershipPlan> findByPlanName(String planName) {
        return SpringDataMembershipPlanRepository.findAll().stream()
                .filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(planName))
                .map(MembershipPlanPersistenceMapper::toDomain)
                .findFirst();
    }

    @Override
    public List<MembershipPlan> findActivePlans() {
        return SpringDataMembershipPlanRepository.findByIsActiveTrue().stream()
                .map(MembershipPlanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MembershipPlan> findInactivePlans() {
        return SpringDataMembershipPlanRepository.findAll().stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsActive()))
                .map(MembershipPlanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MembershipPlan> findByPriceRange(Double minPrice, Double maxPrice) {
        return SpringDataMembershipPlanRepository.findAll().stream()
                .filter(p -> p.getPrice() != null 
                        && p.getPrice().doubleValue() >= minPrice 
                        && p.getPrice().doubleValue() <= maxPrice)
                .map(MembershipPlanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MembershipPlan> findByDurationDaysLessThanEqual(Integer maxDays) {
        return SpringDataMembershipPlanRepository.findAll().stream()
                .filter(p -> p.getDurationDays() != null && p.getDurationDays() <= maxDays)
                .map(MembershipPlanPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByPlanName(String planName) {
        return SpringDataMembershipPlanRepository.existsByName(planName);
    }
}


