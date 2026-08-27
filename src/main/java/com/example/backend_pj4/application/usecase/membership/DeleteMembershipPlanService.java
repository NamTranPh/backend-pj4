package com.example.backend_pj4.application.usecase.membership;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.port.in.membership.DeleteMembershipPlanUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.NotificationReferenceType;
import com.example.backend_pj4.common.constants.enums.NotificationType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Membership;
import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.model.Notification;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.domain.repository.NotificationRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeleteMembershipPlanService implements DeleteMembershipPlanUseCase {

    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipRepository membershipRepository;
    private final NotificationRepository notificationRepository;

    public DeleteMembershipPlanService(
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
    public void execute(String id) {
        MembershipPlan plan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBERSHIP_PLAN_NOT_FOUND));

        List<Membership> activeMembers = membershipRepository.findActiveByPlanId(id);

        for (Membership membership : activeMembers) {
            Membership deactivated = membership.toBuilder()
                    .isActive(false)
                    .deletedAt(LocalDateTime.now())
                    .build();
            membershipRepository.save(deactivated);

            // ponytail: refund cố định 1000 VND, thay bằng PayOS refund API khi integration xong
            log.info("Refund placeholder 1000 VND for userId={} planId={}", membership.getUserId(), id);

            Notification notification = Notification.builder()
                    .userId(membership.getUserId())
                    .type(NotificationType.SYSTEM)
                    .title("Gói đăng ký đã bị ngừng cung cấp")
                    .content("Gói \"" + plan.getName() + "\" đã bị ngừng cung cấp. Bạn sẽ được hoàn tiền 1.000 VNĐ.")
                    .referenceType(NotificationReferenceType.MEMBERSHIP)
                    .referenceId(plan.getId())
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
        }

        MembershipPlan deactivatedPlan = plan.toBuilder()
                .isActive(false)
                .build();
        membershipPlanRepository.save(deactivatedPlan);
    }
}
