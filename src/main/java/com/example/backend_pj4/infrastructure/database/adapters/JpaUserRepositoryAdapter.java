package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.database.entities.UserJpaEntity;
import com.example.backend_pj4.infrastructure.database.mappers.UserPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.UserJpaRepository;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    public JpaUserRepositoryAdapter(UserJpaRepository userJpaRepository, UserPersistenceMapper userPersistenceMapper) {
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
    public List<User> findAll() {
        return userJpaRepository.findAll().stream().map(userPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return userJpaRepository.findByEmailIgnoreCase(email).map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userJpaRepository.findByPhone(phone).map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailOrPhone(String email, String phone) {
        return userJpaRepository.findByEmailOrPhone(email, phone).map(userPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userJpaRepository.existsByPhone(phone);
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return userJpaRepository.findByRole(role).stream().map(userPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findByAccountStatus(AccountStatus status) {
        return userJpaRepository.findByAccountStatus(status).stream().map(userPersistenceMapper::toDomain).collect(Collectors.toList());
    }
}


