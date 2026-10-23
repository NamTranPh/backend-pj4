package com.example.backend_pj4.application.usecase.auth;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.mapper.UserResultMapper;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Membership;
import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class GetCurrentUserService implements GetCurrentUserUseCase {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final UserResultMapper userResultMapper;

    public GetCurrentUserService(
            UserRepository userRepository,
            MembershipRepository membershipRepository,
            MembershipPlanRepository membershipPlanRepository,
            UserResultMapper userResultMapper
    ) {
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.userResultMapper = userResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResult execute(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        var activeMembership = membershipRepository.findActiveByUserId(user.getId());
        boolean isVip = activeMembership.isPresent();
        String currentPlanName = null;
        LocalDate membershipEndDate = null;

        if (activeMembership.isPresent()) {
            Membership membership = activeMembership.get();
            membershipEndDate = membership.getEndDate();
            if (membership.getMembershipPlanId() != null) {
                currentPlanName = membershipPlanRepository.findById(membership.getMembershipPlanId())
                        .map(MembershipPlan::getName)
                        .orElse(null);
            }
        }

        return userResultMapper.toResult(user, isVip, currentPlanName, membershipEndDate);
    }
}
