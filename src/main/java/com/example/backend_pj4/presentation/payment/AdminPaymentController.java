package com.example.backend_pj4.presentation.payment;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.payment.AdminPaymentDetailResult;
import com.example.backend_pj4.application.dto.payment.AdminPaymentStatisticsResult;
import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.application.port.in.payment.CancelPaymentAdminUseCase;
import com.example.backend_pj4.application.port.in.payment.GetPaymentDetailAdminQuery;
import com.example.backend_pj4.application.port.in.payment.GetPaymentStatisticsAdminQuery;
import com.example.backend_pj4.application.port.in.payment.ListPaymentsAdminQuery;
import com.example.backend_pj4.application.port.in.payment.SyncPaymentAdminUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.presentation.common.request.PageableFactory;
import com.example.backend_pj4.presentation.payment.request.AdminCancelPaymentRequest;
import com.example.backend_pj4.presentation.payment.request.RequestAdminPaymentDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Payments - Admin")
@AuthRequired
@RestController
@RequestMapping("/api/v1/admin/payments")
public class AdminPaymentController {

    private final ListPaymentsAdminQuery listPaymentsAdminQuery;
    private final GetPaymentDetailAdminQuery getPaymentDetailAdminQuery;
    private final SyncPaymentAdminUseCase syncPaymentAdminUseCase;
    private final CancelPaymentAdminUseCase cancelPaymentAdminUseCase;
    private final GetPaymentStatisticsAdminQuery getPaymentStatisticsAdminQuery;

    public AdminPaymentController(
            ListPaymentsAdminQuery listPaymentsAdminQuery,
            GetPaymentDetailAdminQuery getPaymentDetailAdminQuery,
            SyncPaymentAdminUseCase syncPaymentAdminUseCase,
            CancelPaymentAdminUseCase cancelPaymentAdminUseCase,
            GetPaymentStatisticsAdminQuery getPaymentStatisticsAdminQuery
    ) {
        this.listPaymentsAdminQuery = listPaymentsAdminQuery;
        this.getPaymentDetailAdminQuery = getPaymentDetailAdminQuery;
        this.syncPaymentAdminUseCase = syncPaymentAdminUseCase;
        this.cancelPaymentAdminUseCase = cancelPaymentAdminUseCase;
        this.getPaymentStatisticsAdminQuery = getPaymentStatisticsAdminQuery;
    }

    @Operation(summary = "Lấy danh sách đơn thanh toán. Quyền truy cập: ADMIN.")
    @GetMapping
    public PageResult<PaymentOrderResult> list(
            @ParameterObject @ModelAttribute RequestAdminPaymentDto query
    ) {
        return listPaymentsAdminQuery.execute(
                query.toFilter(),
                PageableFactory.create(query, ListPaymentsAdminQuery.SORTABLE, ListPaymentsAdminQuery.DEFAULT_SORT)
        );
    }

    @Operation(summary = "Xem chi tiết một đơn thanh toán. Quyền truy cập: ADMIN.")
    @GetMapping("/{id}")
    public ResponseEntity<AdminPaymentDetailResult> getDetail(@PathVariable String id) {
        return ResponseEntity.ok(getPaymentDetailAdminQuery.execute(id));
    }

    @Operation(summary = "Đồng bộ thủ công trạng thái đơn thanh toán trực tiếp từ PayOS. Quyền truy cập: ADMIN.")
    @PostMapping("/{id}/sync")
    public ResponseEntity<PaymentOrderResult> syncStatus(@PathVariable String id) {
        return ResponseEntity.ok(syncPaymentAdminUseCase.execute(id));
    }

    @Operation(summary = "Admin hủy đơn thanh toán đang chờ (PENDING). Quyền truy cập: ADMIN.")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentOrderResult> cancel(
            @PathVariable String id,
            @Valid @RequestBody AdminCancelPaymentRequest request
    ) {
        return ResponseEntity.ok(cancelPaymentAdminUseCase.execute(id, request.reason()));
    }

    @Operation(summary = "Thống kê báo cáo doanh thu & tỷ lệ giao dịch. Quyền truy cập: ADMIN.")
    @GetMapping("/statistics")
    public ResponseEntity<AdminPaymentStatisticsResult> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(getPaymentStatisticsAdminQuery.execute(fromDate, toDate));
    }
}
