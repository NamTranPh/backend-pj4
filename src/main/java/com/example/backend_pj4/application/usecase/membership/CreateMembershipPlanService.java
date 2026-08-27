package com.example.backend_pj4.application.usecase.membership;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.membership.CreateMembershipPlanCommand;
import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;
import com.example.backend_pj4.application.mapper.MembershipPlanResultMapper;
import com.example.backend_pj4.application.port.in.membership.CreateMembershipPlanUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.util.SlugUtils;
import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;

@Service
public class CreateMembershipPlanService implements CreateMembershipPlanUseCase {

    private final MembershipPlanRepository membershipPlanRepository;

    public CreateMembershipPlanService(MembershipPlanRepository membershipPlanRepository) {
        this.membershipPlanRepository = membershipPlanRepository;
    }

    @Override
    @Transactional
    public MembershipPlanResult execute(CreateMembershipPlanCommand command) {
        if (membershipPlanRepository.existsByPlanName(command.name())) {
            throw new CustomException(ErrorCode.MEMBERSHIP_PLAN_NAME_ALREADY_EXISTS);
        }

        String slug = SlugUtils.generateSlug(command.name());
        if (membershipPlanRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        MembershipPlan plan = MembershipPlan.builder()
                .name(command.name())
                .slug(slug)
                .price(command.price())
                .durationDays(command.durationDays())
                .maxDevices(command.maxDevices())
                .canDownload(command.canDownload())
                .videoQuality(command.videoQuality())
                .description(command.description())
                .isActive(true)
                .build();

        MembershipPlan saved = membershipPlanRepository.save(plan);
        return MembershipPlanResultMapper.toResult(saved);
    }
}
