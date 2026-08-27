package com.example.backend_pj4.application.port.in.membership;

import com.example.backend_pj4.application.command.membership.CreateMembershipPlanCommand;
import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;

public interface CreateMembershipPlanUseCase {
    MembershipPlanResult execute(CreateMembershipPlanCommand command);
}
