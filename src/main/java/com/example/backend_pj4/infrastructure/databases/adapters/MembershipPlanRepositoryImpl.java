package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.entities.MembershipPlan;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.MembershipPlanMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaMembershipPlanRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MembershipPlanRepositoryImpl implements MembershipPlanRepository {

    private final JpaMembershipPlanRepository jpaMembershipPlanRepository;
    private final MembershipPlanMapper membershipPlanMapper;

    @Override
    public MembershipPlan save(MembershipPlan plan) {
        var entity = membershipPlanMapper.toEntity(plan);
        var saved = jpaMembershipPlanRepository.save(entity);
        return membershipPlanMapper.toDomain(saved);
    }

    @Override
    public Optional<MembershipPlan> findById(String planId) {
        return jpaMembershipPlanRepository.findById(planId)
                .map(membershipPlanMapper::toDomain);
    }

    @Override
    public List<MembershipPlan> findAll() {
        return jpaMembershipPlanRepository.findAll().stream()
                .map(membershipPlanMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String planId) {
        jpaMembershipPlanRepository.deleteById(planId);
    }

    @Override
    public Optional<MembershipPlan> findByPlanName(String planName) {
        return jpaMembershipPlanRepository.findAll().stream()
                .filter(p -> p.getPlanName().equalsIgnoreCase(planName))
                .map(membershipPlanMapper::toDomain)
                .findFirst();
    }

    @Override
    public List<MembershipPlan> findActivePlans() {
        return jpaMembershipPlanRepository.findByIsActiveTrue().stream()
                .map(membershipPlanMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MembershipPlan> findInactivePlans() {
        return jpaMembershipPlanRepository.findAll().stream()
                .filter(p -> !Boolean.TRUE.equals(p.getIsActive()))
                .map(membershipPlanMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MembershipPlan> findByPriceRange(Double minPrice, Double maxPrice) {
        return jpaMembershipPlanRepository.findAll().stream()
                .filter(p -> p.getPrice() != null 
                        && p.getPrice().doubleValue() >= minPrice 
                        && p.getPrice().doubleValue() <= maxPrice)
                .map(membershipPlanMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MembershipPlan> findByDurationDaysLessThanEqual(Integer maxDays) {
        return jpaMembershipPlanRepository.findAll().stream()
                .filter(p -> p.getDurationDays() != null && p.getDurationDays() <= maxDays)
                .map(membershipPlanMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByPlanName(String planName) {
        return jpaMembershipPlanRepository.existsByPlanName(planName);
    }
}
