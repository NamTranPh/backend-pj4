package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.domain.repository.filter.UserFilter;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;
import com.example.backend_pj4.infrastructure.database.mappers.UserPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.UserJpaRepository;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    public JpaUserRepositoryAdapter(UserJpaRepository userJpaRepository,
                                    UserPersistenceMapper userPersistenceMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userPersistenceMapper = userPersistenceMapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = userPersistenceMapper.toEntity(user);
        return userPersistenceMapper.toDomain(userJpaRepository.save(entity));
    }

    @Override
    public Optional<User> findById(String id) {
        return userJpaRepository.findById(id).map(userPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<User> find(UserFilter filter, PageQuery query) {
        return PageableMapper.toPageResult(
                userJpaRepository.searchAll(filter.q(), PageableMapper.toPageable(query)),
                userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmailIgnoreCase(email).map(userPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(String id) {
        userJpaRepository.deleteById(id);
    }
}
