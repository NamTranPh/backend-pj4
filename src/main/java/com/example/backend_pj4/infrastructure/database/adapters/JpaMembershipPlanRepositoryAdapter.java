package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.domain.repository.filter.MembershipPlanFilter;
import com.example.backend_pj4.infrastructure.database.mappers.MembershipPlanPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.MembershipPlanJpaRepository;

@Repository
public class JpaMembershipPlanRepositoryAdapter implements MembershipPlanRepository {

    private final MembershipPlanJpaRepository membershipPlanJpaRepository;
    private final MembershipPlanPersistenceMapper membershipPlanPersistenceMapper;

    public JpaMembershipPlanRepositoryAdapter(MembershipPlanJpaRepository membershipPlanJpaRepository,
                                              MembershipPlanPersistenceMapper membershipPlanPersistenceMapper) {
        this.membershipPlanJpaRepository = membershipPlanJpaRepository;
        this.membershipPlanPersistenceMapper = membershipPlanPersistenceMapper;
    }

    @Override
    public MembershipPlan save(MembershipPlan plan) {
        return membershipPlanPersistenceMapper.toDomain(
                membershipPlanJpaRepository.save(membershipPlanPersistenceMapper.toEntity(plan)));
    }

    @Override
    public Optional<MembershipPlan> findById(String planId) {
        return membershipPlanJpaRepository.findById(planId)
                .map(membershipPlanPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<MembershipPlan> find(MembershipPlanFilter filter, PageQuery query) {
        return PageableMapper.toPageResult(
                membershipPlanJpaRepository.search(filter.q(), filter.activeOnly(),
                        PageableMapper.toPageable(query)),
                membershipPlanPersistenceMapper::toDomain);
    }

    @Override
    public Optional<MembershipPlan> findBySlug(String slug) {
        return membershipPlanJpaRepository.findBySlug(slug)
                .map(membershipPlanPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return membershipPlanJpaRepository.existsBySlug(slug);
    }

    @Override
    public boolean existsByPlanName(String planName) {
        return membershipPlanJpaRepository.existsByName(planName);
    }

    @Override
    public void deleteById(String planId) {
        membershipPlanJpaRepository.deleteById(planId);
    }
}
