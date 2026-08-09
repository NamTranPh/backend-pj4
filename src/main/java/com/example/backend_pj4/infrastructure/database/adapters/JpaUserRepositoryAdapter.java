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
import com.example.backend_pj4.infrastructure.database.repositories.SpringDataUserRepository;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository SpringDataUserRepository;
    private final UserPersistenceMapper UserPersistenceMapper;

    public JpaUserRepositoryAdapter(SpringDataUserRepository SpringDataUserRepository, UserPersistenceMapper UserPersistenceMapper) {
        this.SpringDataUserRepository = SpringDataUserRepository;
        this.UserPersistenceMapper = UserPersistenceMapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserPersistenceMapper.toEntity(user);
        return UserPersistenceMapper.toDomain(SpringDataUserRepository.save(entity));
    }

    @Override
    public Optional<User> findById(String id) {
        return SpringDataUserRepository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return SpringDataUserRepository.findAll().stream().map(UserPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        SpringDataUserRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return SpringDataUserRepository.existsById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return SpringDataUserRepository.findByEmail(email).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return SpringDataUserRepository.findByEmailIgnoreCase(email).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return SpringDataUserRepository.findByPhone(phone).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailOrPhone(String email, String phone) {
        return SpringDataUserRepository.findByEmailOrPhone(email, phone).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return SpringDataUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return SpringDataUserRepository.existsByPhone(phone);
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return SpringDataUserRepository.findByRole(role).stream().map(UserPersistenceMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findByAccountStatus(AccountStatus status) {
        return SpringDataUserRepository.findByAccountStatus(status).stream().map(UserPersistenceMapper::toDomain).collect(Collectors.toList());
    }
}


