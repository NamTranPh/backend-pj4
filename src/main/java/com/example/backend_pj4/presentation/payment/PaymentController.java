package com.example.backend_pj4.presentation.payment;

import java.util.Map;

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

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.application.port.in.payment.CancelMyPaymentOrderUseCase;
import com.example.backend_pj4.application.port.in.payment.CreatePaymentOrderUseCase;
import com.example.backend_pj4.application.port.in.payment.GetPaymentStatusUseCase;
import com.example.backend_pj4.application.port.in.payment.HandlePayOsWebhookUseCase;
import com.example.backend_pj4.application.port.in.payment.ListMyPaymentOrdersQuery;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import com.example.backend_pj4.common.annotation.IgnoreResponseWrapping;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;
import com.example.backend_pj4.presentation.payment.request.CreatePaymentRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Payments")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final CreatePaymentOrderUseCase createPaymentOrderUseCase;
    private final HandlePayOsWebhookUseCase handlePayOsWebhookUseCase;
    private final GetPaymentStatusUseCase getPaymentStatusUseCase;
    private final ListMyPaymentOrdersQuery listMyPaymentOrdersQuery;
    private final CancelMyPaymentOrderUseCase cancelMyPaymentOrderUseCase;

    public PaymentController(
            CreatePaymentOrderUseCase createPaymentOrderUseCase,
            HandlePayOsWebhookUseCase handlePayOsWebhookUseCase,
            GetPaymentStatusUseCase getPaymentStatusUseCase,
            ListMyPaymentOrdersQuery listMyPaymentOrdersQuery,
            CancelMyPaymentOrderUseCase cancelMyPaymentOrderUseCase
    ) {
        this.createPaymentOrderUseCase = createPaymentOrderUseCase;
        this.handlePayOsWebhookUseCase = handlePayOsWebhookUseCase;
        this.getPaymentStatusUseCase = getPaymentStatusUseCase;
        this.listMyPaymentOrdersQuery = listMyPaymentOrdersQuery;
        this.cancelMyPaymentOrderUseCase = cancelMyPaymentOrderUseCase;
    }

    @Operation(summary = "Tạo đơn thanh toán PayOS.")
    @AuthRequired
    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResult> createOrder(
            @Valid @RequestBody CreatePaymentRequest request,
            @AuthResultUser UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        PaymentOrderResult result = createPaymentOrderUseCase.execute(userId, request.planSlug());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Người dùng chủ động hủy đơn thanh toán đang chờ.")
    @AuthRequired
    @PostMapping("/{transactionCode}/cancel")
    public ResponseEntity<PaymentOrderResult> cancelOrder(
            @PathVariable String transactionCode,
            @AuthResultUser UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        PaymentOrderResult result = cancelMyPaymentOrderUseCase.execute(transactionCode, userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "PayOS webhook callback.")
    @IgnoreResponseWrapping
    @PostMapping("/payos/webhook")
    public ResponseEntity<Map<String, Object>> payosWebhook(@RequestBody String body) {
        handlePayOsWebhookUseCase.execute(body);
        return ResponseEntity.ok(Map.of("success", true, "message", "Webhook received"));
    }

    @Operation(summary = "Kiểm tra trạng thái thanh toán.")
    @AuthRequired
    @GetMapping("/{transactionCode}/status")
    public PaymentOrderResult getStatus(
            @PathVariable String transactionCode,
            @AuthResultUser UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        return getPaymentStatusUseCase.execute(transactionCode, userId);
    }

    @Operation(summary = "Lấy lịch sử các đơn thanh toán của tôi. Quyền truy cập: Người dùng đã đăng nhập.")
    @AuthRequired
    @GetMapping("/my-orders")
    public PageResult<PaymentOrderResult> getMyOrders(
            @AuthResultUser UserDetails userDetails,
            @ModelAttribute PageQueryParamDto params
    ) {
        String userId = resolveUserId(userDetails);
        return listMyPaymentOrdersQuery.execute(userId,
                PageableFactory.create(params, ListMyPaymentOrdersQuery.SORTABLE, ListMyPaymentOrdersQuery.DEFAULT_SORT));
    }

    /** userId lấy thẳng từ principal — filter đã nạp sẵn, không cần tra lại database. */
    private String resolveUserId(UserDetails userDetails) {
        return AppUserDetails.userIdOf(userDetails);
    }
}
