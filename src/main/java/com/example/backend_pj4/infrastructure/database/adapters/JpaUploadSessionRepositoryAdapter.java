package com.example.backend_pj4.infrastructure.database.adapters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.domain.model.UploadSession;
import com.example.backend_pj4.domain.repository.UploadSessionRepository;
import com.example.backend_pj4.infrastructure.database.entities.UploadPartJpaEntity;
import com.example.backend_pj4.infrastructure.database.entities.UploadSessionJpaEntity;
import com.example.backend_pj4.infrastructure.database.mappers.UploadSessionPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.UploadSessionJpaRepository;

@Repository
public class JpaUploadSessionRepositoryAdapter implements UploadSessionRepository {

    private final UploadSessionJpaRepository repository;
    private final UploadSessionPersistenceMapper mapper;

    public JpaUploadSessionRepositoryAdapter(UploadSessionJpaRepository repository, UploadSessionPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UploadSession save(UploadSession session) {
        return mapper.toDomain(repository.save(mapper.toEntity(session)));
    }

    @Override
    public Optional<UploadSession> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<UploadSession> findByTargetIdAndStatus(String targetId, String status) {
        return repository.findByTargetIdAndStatus(targetId, status).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<UploadSession> findExpired() {
        return repository.findExpired(LocalDateTime.now()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void addPart(String sessionId, int partNumber, String etag, long sizeBytes) {
        UploadSessionJpaEntity session = repository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Upload session not found: " + sessionId));
        UploadPartJpaEntity part = new UploadPartJpaEntity();
        part.setSession(session);
        part.setPartNumber(partNumber);
        part.setEtag(etag);
        part.setSizeBytes(sizeBytes);
        session.getParts().add(part);
        repository.save(session);
    }
}
