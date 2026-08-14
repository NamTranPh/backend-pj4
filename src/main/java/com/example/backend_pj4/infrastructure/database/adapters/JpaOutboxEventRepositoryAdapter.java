package com.example.backend_pj4.infrastructure.database.adapters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.OutboxEvent;
import com.example.backend_pj4.domain.repository.OutboxEventRepository;
import com.example.backend_pj4.infrastructure.database.mappers.OutboxEventPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.OutboxEventJpaRepository;

@Repository
public class JpaOutboxEventRepositoryAdapter implements OutboxEventRepository {

    private final OutboxEventJpaRepository jpaRepository;
    private final OutboxEventPersistenceMapper mapper;

    public JpaOutboxEventRepositoryAdapter(OutboxEventJpaRepository jpaRepository, OutboxEventPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(event)));
    }

    @Override
    public Optional<OutboxEvent> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<OutboxEvent> findUnprocessed() {
        return jpaRepository.findByProcessedFalseOrderByCreatedAtAsc().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void markAsProcessed(String id) {
        jpaRepository.markAsProcessed(id);
    }

    @Override
    public void deleteProcessedBefore(LocalDateTime threshold) {
        jpaRepository.deleteProcessedBefore(threshold);
    }
}


