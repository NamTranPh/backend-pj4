package com.example.backend_pj4.application.usecase.membership;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.membership.UpdateMembershipPlanCommand;
import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;
import com.example.backend_pj4.application.mapper.MembershipPlanResultMapper;
import com.example.backend_pj4.application.port.in.membership.UpdateMembershipPlanUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.NotificationReferenceType;
import com.example.backend_pj4.common.constants.enums.NotificationType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.util.SlugUtils;
import com.example.backend_pj4.domain.model.Membership;
import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.model.Notification;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.domain.repository.NotificationRepository;

@Service
public class UpdateMembershipPlanService implements UpdateMembershipPlanUseCase {

    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipRepository membershipRepository;
    private final NotificationRepository notificationRepository;

    public UpdateMembershipPlanService(
            MembershipPlanRepository membershipPlanRepository,
            MembershipRepository membershipRepository,
            NotificationRepository notificationRepository
    ) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.membershipRepository = membershipRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public MembershipPlanResult execute(UpdateMembershipPlanCommand command) {
        MembershipPlan plan = membershipPlanRepository.findById(command.id())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBERSHIP_PLAN_NOT_FOUND));

        MembershipPlan.MembershipPlanBuilder builder = plan.toBuilder();

        if (command.name() != null) {
            if (!command.name().equals(plan.getName()) && membershipPlanRepository.existsByPlanName(command.name())) {
                throw new CustomException(ErrorCode.MEMBERSHIP_PLAN_NAME_ALREADY_EXISTS);
            }
            builder.name(command.name());
            String slug = SlugUtils.generateSlug(command.name());
            if (!slug.equals(plan.getSlug()) && membershipPlanRepository.existsBySlug(slug)) {
                slug = slug + "-" + System.currentTimeMillis();
            }
            builder.slug(slug);
        }
        if (command.price() != null) builder.price(command.price());
        if (command.durationDays() != null) builder.durationDays(command.durationDays());
        if (command.maxDevices() != null) builder.maxDevices(command.maxDevices());
        if (command.canDownload() != null) builder.canDownload(command.canDownload());
        if (command.videoQuality() != null) builder.videoQuality(command.videoQuality());
        if (command.description() != null) builder.description(command.description());
        if (command.isActive() != null) builder.isActive(command.isActive());

        MembershipPlan updated = membershipPlanRepository.save(builder.build());

        notifyActiveSubscribers(updated);

        return MembershipPlanResultMapper.toResult(updated);
    }

    private void notifyActiveSubscribers(MembershipPlan plan) {
        List<Membership> activeMembers = membershipRepository.findActiveByPlanId(plan.getId());
        for (Membership membership : activeMembers) {
            Notification notification = Notification.builder()
                    .userId(membership.getUserId())
                    .type(NotificationType.SYSTEM)
                    .title("Gói đăng ký đã được cập nhật")
                    .content("Gói \"" + plan.getName() + "\" mà bạn đang sử dụng đã được cập nhật thông tin.")
                    .referenceType(NotificationReferenceType.MEMBERSHIP)
                    .referenceId(plan.getId())
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
        }
    }
}
