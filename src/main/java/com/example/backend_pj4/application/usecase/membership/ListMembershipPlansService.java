package com.example.backend_pj4.application.usecase.membership;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;
import com.example.backend_pj4.application.mapper.MembershipPlanResultMapper;
import com.example.backend_pj4.application.port.in.membership.ListMembershipPlansUseCase;
import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;

@Service
public class ListMembershipPlansService implements ListMembershipPlansUseCase {

    private final MembershipPlanRepository membershipPlanRepository;

    public ListMembershipPlansService(MembershipPlanRepository membershipPlanRepository) {
        this.membershipPlanRepository = membershipPlanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipPlanResult> execute(boolean activeOnly) {
        List<MembershipPlan> plans = activeOnly
                ? membershipPlanRepository.findActivePlans()
                : membershipPlanRepository.findAll();

        return plans.stream()
                .map(MembershipPlanResultMapper::toResult)
                .toList();
    }
}
