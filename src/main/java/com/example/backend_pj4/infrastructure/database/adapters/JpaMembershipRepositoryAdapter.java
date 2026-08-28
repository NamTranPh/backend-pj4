package com.example.backend_pj4.infrastructure.database.adapters;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.constants.enums.MembershipPaymentStatus;
import com.example.backend_pj4.domain.model.Membership;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.infrastructure.database.mappers.MembershipPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.MembershipJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaMembershipRepositoryAdapter implements MembershipRepository {

    private final MembershipJpaRepository membershipJpaRepository;
    private final MembershipPersistenceMapper membershipPersistenceMapper;

    @Override
    public Membership save(Membership membership) {
        var entity = membershipPersistenceMapper.toEntity(membership);
        var saved = membershipJpaRepository.save(entity);
        return membershipPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Membership> findById(String memberId) {
        return membershipJpaRepository.findById(memberId)
                .map(membershipPersistenceMapper::toDomain);
    }

    @Override
    public List<Membership> findAll() {
        return membershipJpaRepository.findAll().stream()
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String memberId) {
        membershipJpaRepository.deleteById(memberId);
    }

    @Override
    public List<Membership> findByUserId(String userId) {
        return membershipJpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Membership> findActiveByUserId(String userId) {
        return membershipJpaRepository.findActiveMembershipByUserId(userId, LocalDate.now())
                .map(membershipPersistenceMapper::toDomain);
    }

    @Override
    public List<Membership> findByUserIdOrderByEndDateDesc(String userId) {
        return membershipJpaRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByPlanId(String planId) {
        return membershipJpaRepository.findAll().stream()
                .filter(m -> m.getPlan() != null && m.getPlan().getId().equals(planId))
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findActiveByPlanId(String planId) {
        return membershipJpaRepository.findByIsActiveTrue().stream()
                .filter(m -> m.getPlan() != null && m.getPlan().getId().equals(planId))
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByPaymentStatus(String paymentStatus) {
        return membershipJpaRepository.findByPaymentStatus(MembershipPaymentStatus.valueOf(paymentStatus)).stream()
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findActiveMemberships() {
        return membershipJpaRepository.findByIsActiveTrue().stream()
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findExpiredMemberships() {
        return membershipJpaRepository.findByEndDateBetween(LocalDate.MIN, LocalDate.now().minusDays(1)).stream()
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findExpiringMemberships(LocalDate date) {
        return membershipJpaRepository.findByEndDateBetween(date, date.plusDays(7)).stream()
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByAutoRenewalTrue() {
        return membershipJpaRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getAutoRenewal()))
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findExpiringWithAutoRenewal(LocalDate date) {
        return membershipJpaRepository.findByEndDateBetween(date, date.plusDays(7)).stream()
                .filter(m -> Boolean.TRUE.equals(m.getAutoRenewal()))
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByEndDateBetween(LocalDate startDate, LocalDate endDate) {
        return membershipJpaRepository.findByEndDateBetween(startDate, endDate).stream()
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByStartDateBetween(LocalDate startDate, LocalDate endDate) {
        return membershipJpaRepository.findAll().stream()
                .filter(m -> m.getStartDate() != null 
                        && !m.getStartDate().isBefore(startDate) 
                        && !m.getStartDate().isAfter(endDate))
                .map(membershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByPlanId(String planId) {
        return membershipJpaRepository.findAll().stream()
                .filter(m -> m.getPlan() != null && m.getPlan().getId().equals(planId))
                .count();
    }

    @Override
    public long countByPaymentStatus(String paymentStatus) {
        return membershipJpaRepository.countByPaymentStatus(MembershipPaymentStatus.valueOf(paymentStatus));
    }

    @Override
    public long countActiveMemberships() {
        return membershipJpaRepository.countByIsActiveTrue();
    }
}
