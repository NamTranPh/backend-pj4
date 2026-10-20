package com.example.backend_pj4.domain.repository;

import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.repository.filter.MembershipPlanFilter;

public interface MembershipPlanRepository {
    MembershipPlan save(MembershipPlan plan);
    Optional<MembershipPlan> findById(String planId);
    PageResult<MembershipPlan> find(MembershipPlanFilter filter, PageQuery query);
    Optional<MembershipPlan> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsByPlanName(String planName);
    void deleteById(String planId);
}
