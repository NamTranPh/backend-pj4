package com.example.backend_pj4.infrastructure.database.adapters;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.Membership;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.infrastructure.database.mappers.MembershipPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.MembershipJpaRepository;

@Repository
public class JpaMembershipRepositoryAdapter implements MembershipRepository {

    private final MembershipJpaRepository membershipJpaRepository;
    private final MembershipPersistenceMapper membershipPersistenceMapper;

    public JpaMembershipRepositoryAdapter(MembershipJpaRepository membershipJpaRepository,
                                          MembershipPersistenceMapper membershipPersistenceMapper) {
        this.membershipJpaRepository = membershipJpaRepository;
        this.membershipPersistenceMapper = membershipPersistenceMapper;
    }

    @Override
    public Membership save(Membership membership) {
        return membershipPersistenceMapper.toDomain(
                membershipJpaRepository.save(membershipPersistenceMapper.toEntity(membership)));
    }

    @Override
    public Optional<Membership> findById(String memberId) {
        return membershipJpaRepository.findById(memberId).map(membershipPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Membership> findActiveByUserId(String userId) {
        return membershipJpaRepository.findActiveMembershipByUserId(userId, LocalDate.now())
                .map(membershipPersistenceMapper::toDomain);
    }

    @Override
    public List<Membership> findActiveByPlanId(String planId) {
        return membershipJpaRepository.findByIsActiveTrueAndPlan_Id(planId).stream()
                .map(membershipPersistenceMapper::toDomain).toList();
    }

    @Override
    public void deleteById(String memberId) {
        membershipJpaRepository.deleteById(memberId);
    }
}
