package com.example.backend_pj4.presentation.membership;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.membership.CreateMembershipPlanCommand;
import com.example.backend_pj4.application.command.membership.UpdateMembershipPlanCommand;
import com.example.backend_pj4.application.dto.membership.MembershipPlanResult;
import com.example.backend_pj4.application.port.in.membership.CreateMembershipPlanUseCase;
import com.example.backend_pj4.application.port.in.membership.DeleteMembershipPlanUseCase;
import com.example.backend_pj4.application.port.in.membership.GetMembershipPlanUseCase;
import com.example.backend_pj4.application.port.in.membership.ListMembershipPlansUseCase;
import com.example.backend_pj4.application.port.in.membership.UpdateMembershipPlanUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.presentation.membership.request.CreateMembershipPlanRequest;
import com.example.backend_pj4.presentation.membership.request.UpdateMembershipPlanRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Membership Plans - Admin")
@AuthRequired
@RestController
@RequestMapping("/api/v1/admin/membership-plans")
public class AdminMembershipPlanController {

    private final CreateMembershipPlanUseCase createMembershipPlanUseCase;
    private final UpdateMembershipPlanUseCase updateMembershipPlanUseCase;
    private final GetMembershipPlanUseCase getMembershipPlanUseCase;
    private final ListMembershipPlansUseCase listMembershipPlansUseCase;
    private final DeleteMembershipPlanUseCase deleteMembershipPlanUseCase;

    public AdminMembershipPlanController(
            CreateMembershipPlanUseCase createMembershipPlanUseCase,
            UpdateMembershipPlanUseCase updateMembershipPlanUseCase,
            GetMembershipPlanUseCase getMembershipPlanUseCase,
            ListMembershipPlansUseCase listMembershipPlansUseCase,
            DeleteMembershipPlanUseCase deleteMembershipPlanUseCase
    ) {
        this.createMembershipPlanUseCase = createMembershipPlanUseCase;
        this.updateMembershipPlanUseCase = updateMembershipPlanUseCase;
        this.getMembershipPlanUseCase = getMembershipPlanUseCase;
        this.listMembershipPlansUseCase = listMembershipPlansUseCase;
        this.deleteMembershipPlanUseCase = deleteMembershipPlanUseCase;
    }

    @Operation(summary = "Tạo gói membership mới. Quyền truy cập: ADMIN.")
    @PostMapping
    public ResponseEntity<MembershipPlanResult> create(@Valid @RequestBody CreateMembershipPlanRequest request) {
        MembershipPlanResult result = createMembershipPlanUseCase.execute(
                new CreateMembershipPlanCommand(
                        request.name(),
                        request.price(),
                        request.durationDays(),
                        request.maxDevices(),
                        request.canDownload(),
                        request.videoQuality(),
                        request.description()
                ));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Cập nhật gói membership theo ID. Quyền truy cập: ADMIN.")
    @PatchMapping("/{id}")
    public ResponseEntity<MembershipPlanResult> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateMembershipPlanRequest request
    ) {
        MembershipPlanResult result = updateMembershipPlanUseCase.execute(
                new UpdateMembershipPlanCommand(
                        id,
                        request.name(),
                        request.price(),
                        request.durationDays(),
                        request.maxDevices(),
                        request.canDownload(),
                        request.videoQuality(),
                        request.description(),
                        request.isActive()
                ));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Lấy chi tiết gói membership theo ID. Quyền truy cập: ADMIN.")
    @GetMapping("/{id}")
    public ResponseEntity<MembershipPlanResult> getById(@PathVariable String id) {
        return ResponseEntity.ok(getMembershipPlanUseCase.execute(id));
    }

    @Operation(summary = "Lấy danh sách tất cả gói membership. Quyền truy cập: ADMIN.")
    @GetMapping
    public List<MembershipPlanResult> list() {
        return listMembershipPlansUseCase.execute(false);
    }

    @Operation(summary = "Xóa (vô hiệu hóa) gói membership theo ID. Thông báo và hoàn tiền cho subscribers. Quyền truy cập: ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteMembershipPlanUseCase.execute(id);
        return ResponseEntity.ok().build();
    }
}
