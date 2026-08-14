package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.common.constants.enums.OtpType;
import com.example.backend_pj4.domain.model.OtpVerification;
import com.example.backend_pj4.domain.repository.OtpVerificationRepository;
import com.example.backend_pj4.infrastructure.database.mappers.OtpVerificationPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.OtpVerificationJpaRepository;

@Repository
public class JpaOtpVerificationRepositoryAdapter implements OtpVerificationRepository {

    private final OtpVerificationJpaRepository jpaRepository;
    private final OtpVerificationPersistenceMapper mapper;

    public JpaOtpVerificationRepositoryAdapter(OtpVerificationJpaRepository jpaRepository, OtpVerificationPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public OtpVerification save(OtpVerification otpVerification) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(otpVerification)));
    }

    @Override
    public Optional<OtpVerification> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<OtpVerification> findByEmailAndType(String email, OtpType type) {
        return jpaRepository.findFirstByEmailAndTypeOrderByCreatedAtDesc(email, type).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteExpired() {
        jpaRepository.deleteExpired();
    }
}


