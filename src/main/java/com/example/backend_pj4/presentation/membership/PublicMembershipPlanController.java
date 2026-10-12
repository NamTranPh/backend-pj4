package com.example.backend_pj4.presentation.membership;


import com.example.backend_pj4.common.pagination.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;
import com.example.backend_pj4.application.port.in.membership.ListMembershipPlansQuery;
import com.example.backend_pj4.domain.repository.filter.MembershipPlanFilter;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Membership Plans - Public")
@RestController
@RequestMapping("/api/v1/membership-plans")
public class PublicMembershipPlanController {

    private final ListMembershipPlansQuery listMembershipPlansQuery;

    public PublicMembershipPlanController(ListMembershipPlansQuery listMembershipPlansQuery) {
        this.listMembershipPlansQuery = listMembershipPlansQuery;
    }

    @Operation(summary = "Lấy danh sách gói membership đang hoạt động. Truy cập công khai.")
    @GetMapping
    public PageResult<MembershipPlanResult> listActivePlans(@ModelAttribute PageQueryParamDto params) {
        return listMembershipPlansQuery.execute(
                new MembershipPlanFilter(params.search(), true),
                PageableFactory.create(params,
                        ListMembershipPlansQuery.SORTABLE, ListMembershipPlansQuery.DEFAULT_SORT));
    }
}
