package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.Notification;
import com.example.backend_pj4.domain.repository.NotificationRepository;
import com.example.backend_pj4.infrastructure.database.mappers.NotificationPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.SpringDataNotificationRepository;

@Repository
public class JpaNotificationRepositoryAdapter implements NotificationRepository {

    private final SpringDataNotificationRepository jpaRepository;
    private final NotificationPersistenceMapper mapper;

    public JpaNotificationRepositoryAdapter(SpringDataNotificationRepository jpaRepository, NotificationPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Notification save(Notification notification) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(notification)));
    }

    @Override
    public Optional<Notification> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Notification> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Notification> findByUserId(String userId) {
        return jpaRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadByUserId(String userId) {
        return jpaRepository.findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(userId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countUnreadByUserId(String userId) {
        return jpaRepository.countByUser_IdAndIsReadFalse(userId);
    }

    @Override
    public void markAsRead(String id) {
        jpaRepository.markAsRead(id);
    }
}


