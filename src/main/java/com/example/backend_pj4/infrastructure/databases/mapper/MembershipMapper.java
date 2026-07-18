package com.example.backend_pj4.infrastructure.databases.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.Membership;
import com.example.backend_pj4.infrastructure.databases.entities.MembershipEntity;

@Component
public class MembershipMapper {

    private final UserMapper userMapper;
    private final MembershipPlanMapper membershipPlanMapper;
    private final TransactionMapper transactionMapper;

    public MembershipMapper(
            UserMapper userMapper,
            MembershipPlanMapper membershipPlanMapper,
            @Lazy TransactionMapper transactionMapper) {
        this.userMapper = userMapper;
        this.membershipPlanMapper = membershipPlanMapper;
        this.transactionMapper = transactionMapper;
    }

    public Membership toDomain(MembershipEntity entity) {
        if (entity == null) return null;

        return Membership.builder()
                .memberId(entity.getMemberId())
                .user(userMapper.toDomain(entity.getUser()))
                .plan(membershipPlanMapper.toDomain(entity.getPlan()))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .paymentStatus(entity.getPaymentStatus())
                .autoRenewal(entity.getAutoRenewal())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public MembershipEntity toEntity(Membership domain) {
        if (domain == null) return null;

        MembershipEntity entity = new MembershipEntity();
        entity.setMemberId(domain.getMemberId());
        entity.setUser(userMapper.toEntity(domain.getUser()));
        entity.setPlan(membershipPlanMapper.toEntity(domain.getPlan()));
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setPaymentStatus(domain.getPaymentStatus());
        entity.setAutoRenewal(domain.getAutoRenewal());
        entity.setIsActive(domain.getIsActive());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public List<Membership> toDomainList(List<MembershipEntity> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<MembershipEntity> toEntityList(List<Membership> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public Membership toSimpleDomain(MembershipEntity entity) {
        if (entity == null) return null;
        return Membership.builder()
                .memberId(entity.getMemberId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .paymentStatus(entity.getPaymentStatus())
                .isActive(entity.getIsActive())
                .build();
    }
}
