package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Actor;
import com.example.backend_pj4.domain.repository.ActorRepository;
import com.example.backend_pj4.domain.repository.filter.ActorFilter;
import com.example.backend_pj4.infrastructure.database.mappers.ActorPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.ActorJpaRepository;

@Repository
public class JpaActorRepositoryAdapter implements ActorRepository {

    private final ActorJpaRepository actorJpaRepository;
    private final ActorPersistenceMapper actorPersistenceMapper;

    public JpaActorRepositoryAdapter(ActorJpaRepository actorJpaRepository,
                                     ActorPersistenceMapper actorPersistenceMapper) {
        this.actorJpaRepository = actorJpaRepository;
        this.actorPersistenceMapper = actorPersistenceMapper;
    }

    @Override
    public Actor save(Actor actor) {
        return actorPersistenceMapper.toDomain(
                actorJpaRepository.save(actorPersistenceMapper.toEntity(actor)));
    }

    @Override
    public PageResult<Actor> find(ActorFilter filter, PageQuery query) {
        return PageableMapper.toPageResult(
                actorJpaRepository.search(
                        filter != null ? filter.q() : null,
                        filter != null ? filter.nationality() : null,
                        PageableMapper.toPageable(query)),
                actorPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Actor> findById(String id) {
        return actorJpaRepository.findById(id).map(actorPersistenceMapper::toDomain);
    }

    @Override
    public List<Actor> findByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return actorJpaRepository.findAllById(ids).stream()
                .map(actorPersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsByName(String name) {
        return actorJpaRepository.existsByName(name);
    }

    @Override
    public void deleteById(String id) {
        actorJpaRepository.deleteById(id);
    }
}
