package com.example.backend_pj4.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.domain.model.Notification;
import com.example.backend_pj4.infrastructure.database.entities.NotificationJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;

@Component
public class NotificationPersistenceMapper {

    public Notification toDomain(NotificationJpaEntity entity) {
        if (entity == null) return null;
        return Notification.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .type(entity.getType())
                .title(entity.getTitle())
                .content(entity.getContent())
                .referenceType(entity.getReferenceType())
                .referenceId(entity.getReferenceId())
                .isRead(entity.getIsRead())
                .readAt(entity.getReadAt())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public NotificationJpaEntity toEntity(Notification domain) {
        if (domain == null) return null;
        NotificationJpaEntity entity = new NotificationJpaEntity();
        entity.setId(domain.getId());
        entity.setType(domain.getType());
        entity.setTitle(domain.getTitle());
        entity.setContent(domain.getContent());
        entity.setReferenceType(domain.getReferenceType());
        entity.setReferenceId(domain.getReferenceId());
        entity.setIsRead(domain.getIsRead());
        entity.setReadAt(domain.getReadAt());
        entity.setDeletedAt(domain.getDeletedAt());
        if (domain.getUserId() != null) {
            UserJpaEntity user = new UserJpaEntity();
            user.setId(domain.getUserId());
            entity.setUser(user);
        }
        return entity;
    }
}


