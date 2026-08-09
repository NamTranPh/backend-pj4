package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.constants.enums.ReportStatus;
import com.example.backend_pj4.common.constants.enums.ReportTargetType;
import com.example.backend_pj4.domain.model.Report;

public interface ReportRepository {
    Report save(Report report);
    Optional<Report> findById(String id);
    List<Report> findAll();
    void deleteById(String id);
    List<Report> findByReporterId(String reporterId);
    List<Report> findByTargetTypeAndTargetId(ReportTargetType targetType, String targetId);
    List<Report> findByStatus(ReportStatus status);
    List<Report> findPendingReports();
    long countByStatus(ReportStatus status);
}
