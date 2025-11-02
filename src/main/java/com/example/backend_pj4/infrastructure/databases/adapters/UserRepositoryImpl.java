package com.example.backend_pj4.infrastructure.databases.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.databases.mapper.UserMapper;
import com.example.backend_pj4.infrastructure.databases.repository.JpaUserRepository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository, UserMapper userMapper) {
        this.jpaUserRepository = jpaUserRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        var entity = userMapper.toEntity(user);
        var savedEntity = jpaUserRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(String userId) {
        return jpaUserRepository.findById(userId)
                .map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String userId) {
        jpaUserRepository.deleteById(userId);
    }

    @Override
    public boolean existsById(String userId) {
        return jpaUserRepository.existsById(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return jpaUserRepository.findByPhone(phone)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailOrPhone(String identifier) {
        return jpaUserRepository.findByEmailOrPhone(identifier)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return jpaUserRepository.existsByPhone(phone);
    }

    @Override
    public List<User> findByMembershipStatus(String membershipStatus) {
        return jpaUserRepository.findByMembershipStatus(membershipStatus).stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findExpiredMemberships() {
        return jpaUserRepository.findExpiredMemberships().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByRole(String roleId) {
        return jpaUserRepository.findByRole(roleId).stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findActiveUsers() {
        return jpaUserRepository.findByIsActiveTrue().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findInactiveUsers() {
        return jpaUserRepository.findByIsActiveFalse().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }
}
