package com.example.backend_pj4.presentation.membership;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;
import com.example.backend_pj4.application.port.in.membership.ListMembershipPlansUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Membership Plans - Public")
@RestController
@RequestMapping("/api/v1/membership-plans")
public class PublicMembershipPlanController {

    private final ListMembershipPlansUseCase listMembershipPlansUseCase;

    public PublicMembershipPlanController(ListMembershipPlansUseCase listMembershipPlansUseCase) {
        this.listMembershipPlansUseCase = listMembershipPlansUseCase;
    }

    @Operation(summary = "Lấy danh sách gói membership đang hoạt động. Truy cập công khai.")
    @GetMapping
    public List<MembershipPlanResult> listActivePlans() {
        return listMembershipPlansUseCase.execute(true);
    }
}
