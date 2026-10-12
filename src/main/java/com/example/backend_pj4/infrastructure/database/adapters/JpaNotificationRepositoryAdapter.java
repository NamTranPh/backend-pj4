package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Notification;
import com.example.backend_pj4.domain.repository.NotificationRepository;
import com.example.backend_pj4.domain.repository.filter.NotificationFilter;
import com.example.backend_pj4.infrastructure.database.mappers.NotificationPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.NotificationJpaRepository;

@Repository
public class JpaNotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;
    private final NotificationPersistenceMapper mapper;

    public JpaNotificationRepositoryAdapter(NotificationJpaRepository jpaRepository,
                                            NotificationPersistenceMapper mapper) {
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
    public PageResult<Notification> find(NotificationFilter filter, PageQuery query) {
        return PageableMapper.toPageResult(
                jpaRepository.search(filter.userId(), filter.q(), filter.isRead(),
                        PageableMapper.toPageable(query)),
                mapper::toDomain);
    }

    @Override
    public long countUnreadByUserId(String userId) {
        return jpaRepository.countByUser_IdAndIsReadFalse(userId);
    }

    @Override
    public void markAsRead(String id) {
        jpaRepository.markAsRead(id);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
}
