package com.example.backend_pj4.presentation.payment;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.payment.PaymentRefundResult;
import com.example.backend_pj4.application.port.in.payment.CancelRefundUseCase;
import com.example.backend_pj4.application.port.in.payment.CreateRefundUseCase;
import com.example.backend_pj4.application.port.in.payment.ListRefundsAdminQuery;
import com.example.backend_pj4.application.port.in.payment.ProcessRefundUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.common.request.PageableFactory;
import com.example.backend_pj4.presentation.payment.request.AdminCancelRefundRequest;
import com.example.backend_pj4.presentation.payment.request.CreateRefundRequest;
import com.example.backend_pj4.presentation.payment.request.ProcessRefundRequest;
import com.example.backend_pj4.presentation.payment.request.RequestAdminRefundDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Refunds - Admin")
@AuthRequired
@RestController
@RequestMapping("/api/v1/admin/refunds")
public class AdminRefundController {

    private final ListRefundsAdminQuery listRefundsAdminQuery;
    private final CreateRefundUseCase createRefundUseCase;
    private final ProcessRefundUseCase processRefundUseCase;
    private final CancelRefundUseCase cancelRefundUseCase;

    public AdminRefundController(
            ListRefundsAdminQuery listRefundsAdminQuery,
            CreateRefundUseCase createRefundUseCase,
            ProcessRefundUseCase processRefundUseCase,
            CancelRefundUseCase cancelRefundUseCase
    ) {
        this.listRefundsAdminQuery = listRefundsAdminQuery;
        this.createRefundUseCase = createRefundUseCase;
        this.processRefundUseCase = processRefundUseCase;
        this.cancelRefundUseCase = cancelRefundUseCase;
    }

    @Operation(summary = "Lấy danh sách yêu cầu hoàn tiền (Phân trang + Lọc đa tiêu chí). Quyền: ADMIN.")
    @GetMapping
    public PageResult<PaymentRefundResult> list(
            @ParameterObject @ModelAttribute RequestAdminRefundDto query
    ) {
        return listRefundsAdminQuery.execute(
                query.toFilter(),
                PageableFactory.create(query, ListRefundsAdminQuery.SORTABLE, ListRefundsAdminQuery.DEFAULT_SORT)
        );
    }

    @Operation(summary = "Tạo phiếu yêu cầu hoàn tiền cho đơn thanh toán. Quyền: ADMIN.")
    @PostMapping("/request")
    public ResponseEntity<PaymentRefundResult> createRequest(
            @Valid @RequestBody CreateRefundRequest request,
            @AuthResultUser UserDetails userDetails
    ) {
        String adminId = userDetails != null ? AppUserDetails.userIdOf(userDetails) : null;
        PaymentRefundResult result = createRefundUseCase.execute(
                request.paymentOrderId(),
                request.amount(),
                request.reason(),
                adminId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Xác nhận xử lý hoàn tiền thành công (đã chuyển tiền xong, thu hồi VIP). Quyền: ADMIN.")
    @PostMapping("/{id}/process")
    public ResponseEntity<PaymentRefundResult> processRefund(
            @PathVariable String id,
            @Valid @RequestBody ProcessRefundRequest request,
            @AuthResultUser UserDetails userDetails
    ) {
        String adminId = userDetails != null ? AppUserDetails.userIdOf(userDetails) : null;
        PaymentRefundResult result = processRefundUseCase.execute(
                id,
                request.refundTransactionCode(),
                request.adminNote(),
                adminId
        );
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Hủy hoặc từ chối phiếu yêu cầu hoàn tiền. Quyền: ADMIN.")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentRefundResult> cancelRefund(
            @PathVariable String id,
            @RequestBody(required = false) AdminCancelRefundRequest request,
            @AuthResultUser UserDetails userDetails
    ) {
        String adminId = userDetails != null ? AppUserDetails.userIdOf(userDetails) : null;
        String adminNote = request != null ? request.adminNote() : null;
        PaymentRefundResult result = cancelRefundUseCase.execute(id, adminNote, adminId);
        return ResponseEntity.ok(result);
    }
}
