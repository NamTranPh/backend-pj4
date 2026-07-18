package com.example.backend_pj4.application.services.membership;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.common.enums.PaymentStatus;
import com.example.backend_pj4.domain.entities.Membership;
import com.example.backend_pj4.domain.entities.MembershipPlan;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipService extends BaseService {

    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final UserRepository userRepository;

    public Membership subscribe(String userId, String planId, boolean autoRenewal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MembershipPlan plan = membershipPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        Optional<Membership> existingMembership = membershipRepository.findActiveByUserId(userId);
        if (existingMembership.isPresent()) {
            throw new IllegalStateException("User already has an active membership");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(plan.getDurationDays());

        Membership membership = Membership.builder()
                .startDate(startDate)
                .endDate(endDate)
                .paymentStatus(PaymentStatus.PENDING)
                .autoRenewal(autoRenewal)
                .isActive(true)
                .build();

        membership.setUser(user);
        membership.setPlan(plan);

        return membershipRepository.save(membership);
    }

    public Membership activateMembership(String memberId) {
        Membership membership = membershipRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
        membership.setPaymentStatus(PaymentStatus.COMPLETED);
        membership.setIsActive(true);
        return membershipRepository.save(membership);
    }

    @Transactional(readOnly = true)
    public Optional<Membership> getUserActiveMembership(String userId) {
        return membershipRepository.findActiveByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Membership> getUserMemberships(String userId) {
        return membershipRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Membership> getAllActiveMemberships() {
        return membershipRepository.findActiveMemberships();
    }

    @Transactional(readOnly = true)
    public List<MembershipPlan> getActivePlans() {
        return membershipPlanRepository.findActivePlans();
    }

    public MembershipPlan createPlan(String name, java.math.BigDecimal price, Integer durationDays,
            Integer maxDevices, Boolean canDownload, String videoQuality, String description) {
        
        if (membershipPlanRepository.existsByPlanName(name)) {
            throw new IllegalArgumentException("Plan name already exists");
        }

        MembershipPlan plan = MembershipPlan.builder()
                .price(price)
                .durationDays(durationDays)
                .maxDevices(maxDevices != null ? maxDevices : 1)
                .canDownload(canDownload != null ? canDownload : false)
                .videoQuality(videoQuality != null ? videoQuality : "HD")
                .description(description)
                .isActive(true)
                .build();
        plan.setPlanName(name);

        return membershipPlanRepository.save(plan);
    }

    public MembershipPlan updatePlan(String planId, String name, java.math.BigDecimal price,
            Integer durationDays, Integer maxDevices, Boolean canDownload, String videoQuality, String description, Boolean isActive) {
        
        MembershipPlan plan = membershipPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        if (name != null) plan.setPlanName(name);
        if (price != null) plan.setPrice(price);
        if (durationDays != null) plan.setDurationDays(durationDays);
        if (maxDevices != null) plan.setMaxDevices(maxDevices);
        if (canDownload != null) plan.setCanDownload(canDownload);
        if (videoQuality != null) plan.setVideoQuality(videoQuality);
        if (description != null) plan.setDescription(description);
        if (isActive != null) plan.setIsActive(isActive);

        return membershipPlanRepository.save(plan);
    }

    @Transactional(readOnly = true)
    public long countActiveMemberships() {
        return membershipRepository.countActiveMemberships();
    }

    public void cancelMembership(String memberId) {
        Membership membership = membershipRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
        membership.setIsActive(false);
        membershipRepository.save(membership);
    }
}
