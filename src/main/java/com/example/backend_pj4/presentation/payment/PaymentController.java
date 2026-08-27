package com.example.backend_pj4.presentation.payment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.payment.PaymentOrderResult;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.application.port.in.payment.CreatePaymentOrderUseCase;
import com.example.backend_pj4.application.port.in.payment.GetPaymentStatusUseCase;
import com.example.backend_pj4.application.port.in.payment.HandlePayOsWebhookUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;
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
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public PaymentController(
            CreatePaymentOrderUseCase createPaymentOrderUseCase,
            HandlePayOsWebhookUseCase handlePayOsWebhookUseCase,
            GetPaymentStatusUseCase getPaymentStatusUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase
    ) {
        this.createPaymentOrderUseCase = createPaymentOrderUseCase;
        this.handlePayOsWebhookUseCase = handlePayOsWebhookUseCase;
        this.getPaymentStatusUseCase = getPaymentStatusUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @Operation(summary = "Tạo đơn thanh toán PayOS.")
    @AuthRequired
    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResult> createOrder(
            @Valid @RequestBody CreatePaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        PaymentOrderResult result = createPaymentOrderUseCase.execute(userId, request.planSlug());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "PayOS webhook callback.")
    @PostMapping("/payos/webhook")
    public ResponseEntity<Void> payosWebhook(@RequestBody String body) {
        handlePayOsWebhookUseCase.execute(body);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Kiểm tra trạng thái thanh toán.")
    @AuthRequired
    @GetMapping("/{transactionCode}/status")
    public PaymentOrderResult getStatus(
            @PathVariable String transactionCode,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        return getPaymentStatusUseCase.execute(transactionCode, userId);
    }

    private String resolveUserId(UserDetails userDetails) {
        UserProfileResult user = getCurrentUserUseCase.execute(userDetails.getUsername());
        return user.id();
    }
}
