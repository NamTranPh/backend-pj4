package com.example.backend_pj4.application.usecase.membership;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;
import com.example.backend_pj4.application.mapper.MembershipPlanResultMapper;
import com.example.backend_pj4.application.port.in.membership.GetMembershipPlanUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;

@Service
public class GetMembershipPlanService implements GetMembershipPlanUseCase {

    private final MembershipPlanRepository membershipPlanRepository;

    public GetMembershipPlanService(MembershipPlanRepository membershipPlanRepository) {
        this.membershipPlanRepository = membershipPlanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipPlanResult execute(String id) {
        return membershipPlanRepository.findById(id)
                .map(MembershipPlanResultMapper::toResult)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBERSHIP_PLAN_NOT_FOUND));
    }
}
