package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.EmailVerification;
import com.example.backend_pj4.domain.repository.EmailVerificationRepository;
import com.example.backend_pj4.infrastructure.database.mappers.EmailVerificationPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.SpringDataEmailVerificationRepository;

@Repository
public class JpaEmailVerificationRepositoryAdapter implements EmailVerificationRepository {

    private final SpringDataEmailVerificationRepository jpaRepository;
    private final EmailVerificationPersistenceMapper mapper;

    public JpaEmailVerificationRepositoryAdapter(SpringDataEmailVerificationRepository jpaRepository, EmailVerificationPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public EmailVerification save(EmailVerification emailVerification) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(emailVerification)));
    }

    @Override
    public Optional<EmailVerification> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<EmailVerification> findByToken(String token) {
        return jpaRepository.findByToken(token).map(mapper::toDomain);
    }

    @Override
    public List<EmailVerification> findByUserId(String userId) {
        return jpaRepository.findByUser_Id(userId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
}


