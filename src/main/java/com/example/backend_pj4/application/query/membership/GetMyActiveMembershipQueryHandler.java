package com.example.backend_pj4.application.query.membership;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.membership.MyMembershipResult;
import com.example.backend_pj4.application.port.in.membership.GetMyActiveMembershipQuery;
import com.example.backend_pj4.domain.model.Membership;
import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.domain.repository.MembershipRepository;

@Service
public class GetMyActiveMembershipQueryHandler implements GetMyActiveMembershipQuery {

    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;

    public GetMyActiveMembershipQueryHandler(
            MembershipRepository membershipRepository,
            MembershipPlanRepository membershipPlanRepository
    ) {
        this.membershipRepository = membershipRepository;
        this.membershipPlanRepository = membershipPlanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MyMembershipResult execute(String userId) {
        return membershipRepository.findActiveByUserId(userId)
                .map(this::mapToResult)
                .orElse(null);
    }

    private MyMembershipResult mapToResult(Membership membership) {
        MembershipPlan plan = membershipPlanRepository.findById(membership.getMembershipPlanId())
                .orElse(null);

        long daysRemaining = 0;
        if (membership.getEndDate() != null) {
            daysRemaining = Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), membership.getEndDate()));
        }

        return new MyMembershipResult(
                membership.getId(),
                membership.getMembershipPlanId(),
                plan != null ? plan.getName() : null,
                plan != null ? plan.getSlug() : null,
                plan != null ? plan.getPrice() : null,
                plan != null ? plan.getMaxDevices() : null,
                plan != null ? plan.getCanDownload() : null,
                plan != null ? plan.getVideoQuality() : null,
                membership.getStartDate(),
                membership.getEndDate(),
                daysRemaining,
                membership.getAutoRenewal(),
                membership.getIsActive()
        );
    }
}
