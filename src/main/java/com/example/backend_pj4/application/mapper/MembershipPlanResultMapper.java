package com.example.backend_pj4.application.mapper;

import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;
import com.example.backend_pj4.domain.model.MembershipPlan;

public class MembershipPlanResultMapper {

    private MembershipPlanResultMapper() {
    }

    public static MembershipPlanResult toResult(MembershipPlan plan) {
        return new MembershipPlanResult(
                plan.getId(),
                plan.getName(),
                plan.getSlug(),
                plan.getPrice(),
                plan.getDurationDays(),
                plan.getMaxDevices(),
                plan.getCanDownload(),
                plan.getVideoQuality(),
                plan.getDescription(),
                plan.getIsActive(),
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }
}
