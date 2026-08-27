package com.example.backend_pj4.application.port.in.membership;

import java.util.List;

import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;

public interface ListMembershipPlansUseCase {
    List<MembershipPlanResult> execute(boolean activeOnly);
}
