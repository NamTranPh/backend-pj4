package com.example.backend_pj4.application.port.in.membership;

import java.util.Set;

import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.MembershipPlanFilter;

/** Cổng ĐỌC danh sách gói thành viên. */
public interface ListMembershipPlansQuery {

    Set<String> SORTABLE = Set.of("createdAt", "name", "price", "durationDays");
    String DEFAULT_SORT = "price";

    PageResult<MembershipPlanResult> execute(MembershipPlanFilter filter, PageQuery query);
}
