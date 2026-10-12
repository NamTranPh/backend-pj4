package com.example.backend_pj4.application.query.membership;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;
import com.example.backend_pj4.application.mapper.MembershipPlanResultMapper;
import com.example.backend_pj4.application.port.in.membership.ListMembershipPlansQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.domain.repository.filter.MembershipPlanFilter;

@Service
public class ListMembershipPlansQueryHandler implements ListMembershipPlansQuery {

    private final MembershipPlanRepository membershipPlanRepository;

    public ListMembershipPlansQueryHandler(MembershipPlanRepository membershipPlanRepository) {
        this.membershipPlanRepository = membershipPlanRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<MembershipPlanResult> execute(MembershipPlanFilter filter, PageQuery query) {
        return membershipPlanRepository.find(filter, query).map(MembershipPlanResultMapper::toResult);
    }
}
