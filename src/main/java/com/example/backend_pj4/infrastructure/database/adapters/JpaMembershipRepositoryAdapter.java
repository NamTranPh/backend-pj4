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
import com.example.backend_pj4.infrastructure.database.repositories.SpringDataMembershipRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaMembershipRepositoryAdapter implements MembershipRepository {

    private final SpringDataMembershipRepository SpringDataMembershipRepository;
    private final MembershipPersistenceMapper MembershipPersistenceMapper;

    @Override
    public Membership save(Membership membership) {
        var entity = MembershipPersistenceMapper.toEntity(membership);
        var saved = SpringDataMembershipRepository.save(entity);
        return MembershipPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Membership> findById(String memberId) {
        return SpringDataMembershipRepository.findById(memberId)
                .map(MembershipPersistenceMapper::toDomain);
    }

    @Override
    public List<Membership> findAll() {
        return SpringDataMembershipRepository.findAll().stream()
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String memberId) {
        SpringDataMembershipRepository.deleteById(memberId);
    }

    @Override
    public List<Membership> findByUserId(String userId) {
        return SpringDataMembershipRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Membership> findActiveByUserId(String userId) {
        return SpringDataMembershipRepository.findActiveMembershipByUserId(userId, LocalDate.now())
                .map(MembershipPersistenceMapper::toDomain);
    }

    @Override
    public List<Membership> findByUserIdOrderByEndDateDesc(String userId) {
        return SpringDataMembershipRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByPlanId(String planId) {
        return SpringDataMembershipRepository.findAll().stream()
                .filter(m -> m.getPlan() != null && m.getPlan().getId().equals(planId))
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByPaymentStatus(String paymentStatus) {
        return SpringDataMembershipRepository.findByPaymentStatus(MembershipPaymentStatus.valueOf(paymentStatus)).stream()
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findActiveMemberships() {
        return SpringDataMembershipRepository.findByIsActiveTrue().stream()
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findExpiredMemberships() {
        return SpringDataMembershipRepository.findByEndDateBetween(LocalDate.MIN, LocalDate.now().minusDays(1)).stream()
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findExpiringMemberships(LocalDate date) {
        return SpringDataMembershipRepository.findByEndDateBetween(date, date.plusDays(7)).stream()
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByAutoRenewalTrue() {
        return SpringDataMembershipRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getAutoRenewal()))
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findExpiringWithAutoRenewal(LocalDate date) {
        return SpringDataMembershipRepository.findByEndDateBetween(date, date.plusDays(7)).stream()
                .filter(m -> Boolean.TRUE.equals(m.getAutoRenewal()))
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByEndDateBetween(LocalDate startDate, LocalDate endDate) {
        return SpringDataMembershipRepository.findByEndDateBetween(startDate, endDate).stream()
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByStartDateBetween(LocalDate startDate, LocalDate endDate) {
        return SpringDataMembershipRepository.findAll().stream()
                .filter(m -> m.getStartDate() != null 
                        && !m.getStartDate().isBefore(startDate) 
                        && !m.getStartDate().isAfter(endDate))
                .map(MembershipPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByPlanId(String planId) {
        return SpringDataMembershipRepository.findAll().stream()
                .filter(m -> m.getPlan() != null && m.getPlan().getId().equals(planId))
                .count();
    }

    @Override
    public long countByPaymentStatus(String paymentStatus) {
        return SpringDataMembershipRepository.countByPaymentStatus(MembershipPaymentStatus.valueOf(paymentStatus));
    }

    @Override
    public long countActiveMemberships() {
        return SpringDataMembershipRepository.countByIsActiveTrue();
    }
}
