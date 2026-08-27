package com.example.backend_pj4.application.port.in.membership;

import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;

public interface GetMembershipPlanUseCase {
    MembershipPlanResult execute(String id);
}
