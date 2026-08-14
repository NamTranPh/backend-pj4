package com.example.backend_pj4.infrastructure.database.adapters;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.backend_pj4.common.constants.enums.ReportStatus;
import com.example.backend_pj4.common.constants.enums.ReportTargetType;
import com.example.backend_pj4.domain.model.Report;
import com.example.backend_pj4.domain.repository.ReportRepository;
import com.example.backend_pj4.infrastructure.database.mappers.ReportPersistenceMapper;
import com.example.backend_pj4.infrastructure.database.repositories.ReportJpaRepository;

@Repository
public class JpaReportRepositoryAdapter implements ReportRepository {

    private final ReportJpaRepository jpaRepository;
    private final ReportPersistenceMapper mapper;

    public JpaReportRepositoryAdapter(ReportJpaRepository jpaRepository, ReportPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Report save(Report report) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(report)));
    }

    @Override
    public Optional<Report> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Report> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Report> findByReporterId(String reporterId) {
        return jpaRepository.findByReporter_Id(reporterId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Report> findByTargetTypeAndTargetId(ReportTargetType targetType, String targetId) {
        return jpaRepository.findByTargetTypeAndTargetId(targetType, targetId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Report> findByStatus(ReportStatus status) {
        return jpaRepository.findByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Report> findPendingReports() {
        return jpaRepository.findPendingReports().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long countByStatus(ReportStatus status) {
        return jpaRepository.countByStatus(status);
    }
}


