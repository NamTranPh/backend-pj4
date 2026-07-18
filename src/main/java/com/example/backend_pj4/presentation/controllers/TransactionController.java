package com.example.backend_pj4.presentation.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.services.transaction.TransactionService;
import com.example.backend_pj4.common.base.BaseController;
import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.common.enums.PaymentMethod;
import com.example.backend_pj4.domain.entities.Transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions")
public class TransactionController extends BaseController {

    private final TransactionService transactionService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create transaction", description = "Create a new transaction (admin only)")
    public ApiResponseDto<Transaction> createTransaction(
            @RequestParam String userId,
            @RequestParam(required = false) String membershipId,
            @RequestParam BigDecimal amount,
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam(required = false) String notes) {
        Transaction transaction = transactionService.createTransaction(
                userId, membershipId, amount, paymentMethod, notes);
        return ApiResponseDto.success(transaction, "Transaction created successfully");
    }

    @PutMapping("/{transactionId}/complete")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Complete transaction")
    public ApiResponseDto<Transaction> completeTransaction(
            @PathVariable String transactionId,
            @RequestParam(required = false) String gatewayTransactionId) {
        Transaction transaction = transactionService.completeTransaction(transactionId, gatewayTransactionId);
        return ApiResponseDto.success(transaction, "Transaction completed successfully");
    }

    @PutMapping("/{transactionId}/fail")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark transaction as failed")
    public ApiResponseDto<Transaction> failTransaction(@PathVariable String transactionId) {
        Transaction transaction = transactionService.failTransaction(transactionId);
        return ApiResponseDto.success(transaction, "Transaction marked as failed");
    }

    @PutMapping("/{transactionId}/refund")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Refund transaction")
    public ApiResponseDto<Transaction> refundTransaction(@PathVariable String transactionId) {
        Transaction transaction = transactionService.refundTransaction(transactionId);
        return ApiResponseDto.success(transaction, "Transaction refunded successfully");
    }

    @GetMapping("/user/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get user transactions")
    public ApiResponseDto<List<Transaction>> getUserTransactions(@PathVariable String userId) {
        List<Transaction> transactions = transactionService.getUserTransactions(userId);
        return ApiResponseDto.success(transactions, "Transactions retrieved successfully");
    }

    @GetMapping("/code/{code}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get transaction by code")
    public ApiResponseDto<Transaction> getTransactionByCode(@PathVariable String code) {
        return transactionService.getTransactionByCode(code)
                .map(t -> ApiResponseDto.success(t, "Transaction found"))
                .orElse(ApiResponseDto.success(null, "Transaction not found"));
    }

    @GetMapping("/pending")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending transactions")
    public ApiResponseDto<List<Transaction>> getPendingTransactions() {
        List<Transaction> transactions = transactionService.getPendingTransactions();
        return ApiResponseDto.success(transactions, "Pending transactions retrieved");
    }

    @GetMapping("/revenue")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get total revenue")
    public ApiResponseDto<Double> getTotalRevenue() {
        Double revenue = transactionService.getTotalRevenue();
        return ApiResponseDto.success(revenue, "Total revenue retrieved");
    }

    @GetMapping("/revenue/range")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get revenue by date range")
    public ApiResponseDto<Double> getRevenueByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        Double revenue = transactionService.getRevenueByDateRange(startDate, endDate);
        return ApiResponseDto.success(revenue, "Revenue for date range retrieved");
    }
}
