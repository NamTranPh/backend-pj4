package com.example.backend_pj4.infrastructure.databases.adapters;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.enums.PaymentStatus;
import com.example.backend_pj4.domain.entities.Membership;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.MembershipMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaMembershipRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MembershipRepositoryImpl implements MembershipRepository {

    private final JpaMembershipRepository jpaMembershipRepository;
    private final MembershipMapper membershipMapper;

    @Override
    public Membership save(Membership membership) {
        var entity = membershipMapper.toEntity(membership);
        var saved = jpaMembershipRepository.save(entity);
        return membershipMapper.toDomain(saved);
    }

    @Override
    public Optional<Membership> findById(String memberId) {
        return jpaMembershipRepository.findById(memberId)
                .map(membershipMapper::toDomain);
    }

    @Override
    public List<Membership> findAll() {
        return jpaMembershipRepository.findAll().stream()
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String memberId) {
        jpaMembershipRepository.deleteById(memberId);
    }

    @Override
    public List<Membership> findByUserId(String userId) {
        return jpaMembershipRepository.findByUserUserIdOrderByCreatedAtDesc(userId).stream()
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Membership> findActiveByUserId(String userId) {
        return jpaMembershipRepository.findActiveMembershipByUserId(userId, LocalDate.now())
                .map(membershipMapper::toDomain);
    }

    @Override
    public List<Membership> findByUserIdOrderByEndDateDesc(String userId) {
        return jpaMembershipRepository.findByUserUserIdOrderByCreatedAtDesc(userId).stream()
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByPlanId(String planId) {
        return jpaMembershipRepository.findAll().stream()
                .filter(m -> m.getPlan() != null && m.getPlan().getPlanId().equals(planId))
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByPaymentStatus(String paymentStatus) {
        return jpaMembershipRepository.findByPaymentStatus(PaymentStatus.valueOf(paymentStatus)).stream()
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findActiveMemberships() {
        return jpaMembershipRepository.findByIsActiveTrue().stream()
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findExpiredMemberships() {
        return jpaMembershipRepository.findByEndDateBetween(LocalDate.MIN, LocalDate.now().minusDays(1)).stream()
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findExpiringMemberships(LocalDate date) {
        return jpaMembershipRepository.findByEndDateBetween(date, date.plusDays(7)).stream()
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByAutoRenewalTrue() {
        return jpaMembershipRepository.findAll().stream()
                .filter(m -> Boolean.TRUE.equals(m.getAutoRenewal()))
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findExpiringWithAutoRenewal(LocalDate date) {
        return jpaMembershipRepository.findByEndDateBetween(date, date.plusDays(7)).stream()
                .filter(m -> Boolean.TRUE.equals(m.getAutoRenewal()))
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByEndDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaMembershipRepository.findByEndDateBetween(startDate, endDate).stream()
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Membership> findByStartDateBetween(LocalDate startDate, LocalDate endDate) {
        return jpaMembershipRepository.findAll().stream()
                .filter(m -> m.getStartDate() != null 
                        && !m.getStartDate().isBefore(startDate) 
                        && !m.getStartDate().isAfter(endDate))
                .map(membershipMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByPlanId(String planId) {
        return jpaMembershipRepository.findAll().stream()
                .filter(m -> m.getPlan() != null && m.getPlan().getPlanId().equals(planId))
                .count();
    }

    @Override
    public long countByPaymentStatus(String paymentStatus) {
        return jpaMembershipRepository.countByPaymentStatus(PaymentStatus.valueOf(paymentStatus));
    }

    @Override
    public long countActiveMemberships() {
        return jpaMembershipRepository.countByIsActiveTrue();
    }
}
