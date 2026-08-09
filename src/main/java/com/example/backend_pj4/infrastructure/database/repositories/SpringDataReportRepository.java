package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.backend_pj4.common.constants.enums.ReportStatus;
import com.example.backend_pj4.common.constants.enums.ReportTargetType;
import com.example.backend_pj4.infrastructure.database.entities.ReportJpaEntity;

public interface SpringDataReportRepository extends JpaRepository<ReportJpaEntity, String> {
    List<ReportJpaEntity> findByReporter_Id(String reporterId);
    List<ReportJpaEntity> findByTargetTypeAndTargetId(ReportTargetType targetType, String targetId);
    List<ReportJpaEntity> findByStatus(ReportStatus status);

    @Query("SELECT r FROM ReportJpaEntity r WHERE r.status = 'PENDING'")
    List<ReportJpaEntity> findPendingReports();

    long countByStatus(ReportStatus status);
}


