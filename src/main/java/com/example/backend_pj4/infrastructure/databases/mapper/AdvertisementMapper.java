package com.example.backend_pj4.infrastructure.databases.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.entities.Advertisement;
import com.example.backend_pj4.infrastructure.databases.entities.AdvertisementEntity;

@Component
public class AdvertisementMapper {

    private final UserMapper userMapper;

    public AdvertisementMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Advertisement toDomain(AdvertisementEntity entity) {
        if (entity == null) return null;

        return Advertisement.builder()
                .adId(entity.getAdId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .imageUrl(entity.getImageUrl())
                .clickUrl(entity.getClickUrl())
                .adType(entity.getAdType())
                .position(entity.getPosition())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .clickCount(entity.getClickCount())
                .viewCount(entity.getViewCount())
                .isActive(entity.getIsActive())
                .createdBy(entity.getCreatedBy() != null
                        ? userMapper.toSimpleDomain(entity.getCreatedBy())
                        : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public AdvertisementEntity toEntity(Advertisement domain) {
        if (domain == null) return null;

        AdvertisementEntity entity = new AdvertisementEntity();
        entity.setAdId(domain.getAdId());
        entity.setTitle(domain.getTitle());
        entity.setContent(domain.getContent());
        entity.setImageUrl(domain.getImageUrl());
        entity.setClickUrl(domain.getClickUrl());
        entity.setAdType(domain.getAdType());
        entity.setPosition(domain.getPosition());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setClickCount(domain.getClickCount());
        entity.setViewCount(domain.getViewCount());
        entity.setIsActive(domain.getIsActive());
        entity.setCreatedBy(domain.getCreatedBy() != null
                ? userMapper.toEntity(domain.getCreatedBy())
                : null);
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public List<Advertisement> toDomainList(List<AdvertisementEntity> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<AdvertisementEntity> toEntityList(List<Advertisement> domains) {
        if (domains == null || domains.isEmpty()) return List.of();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
