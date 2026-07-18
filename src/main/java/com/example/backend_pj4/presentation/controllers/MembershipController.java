package com.example.backend_pj4.presentation.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.services.membership.MembershipService;
import com.example.backend_pj4.common.base.BaseController;
import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.domain.entities.Membership;
import com.example.backend_pj4.domain.entities.MembershipPlan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
@Tag(name = "Memberships")
public class MembershipController extends BaseController {

    private final MembershipService membershipService;

    @PostMapping("/subscribe")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Subscribe to a membership plan")
    public ApiResponseDto<Membership> subscribe(
            @RequestParam String userId,
            @RequestParam String planId,
            @RequestParam(defaultValue = "false") boolean autoRenewal) {
        Membership membership = membershipService.subscribe(userId, planId, autoRenewal);
        return ApiResponseDto.success(membership, "Subscription created successfully");
    }

    @GetMapping("/user/{userId}/active")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get user's active membership")
    public ApiResponseDto<Membership> getActiveMembership(@PathVariable String userId) {
        return membershipService.getUserActiveMembership(userId)
                .map(m -> ApiResponseDto.success(m, "Active membership found"))
                .orElse(ApiResponseDto.success(null, "No active membership found"));
    }

    @GetMapping("/user/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get user's all memberships")
    public ApiResponseDto<List<Membership>> getUserMemberships(@PathVariable String userId) {
        List<Membership> memberships = membershipService.getUserMemberships(userId);
        return ApiResponseDto.success(memberships, "Memberships retrieved successfully");
    }

    @GetMapping("/plans")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get all active membership plans")
    public ApiResponseDto<List<MembershipPlan>> getActivePlans() {
        List<MembershipPlan> plans = membershipService.getActivePlans();
        return ApiResponseDto.success(plans, "Plans retrieved successfully");
    }

    @PostMapping("/plans")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create membership plan")
    public ApiResponseDto<MembershipPlan> createPlan(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam Integer durationDays,
            @RequestParam(required = false) Integer maxDevices,
            @RequestParam(required = false) Boolean canDownload,
            @RequestParam(required = false) String videoQuality,
            @RequestParam(required = false) String description) {
        MembershipPlan plan = membershipService.createPlan(
                name, price, durationDays, maxDevices, canDownload, videoQuality, description);
        return ApiResponseDto.success(plan, "Plan created successfully");
    }

    @PutMapping("/plans/{planId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update membership plan")
    public ApiResponseDto<MembershipPlan> updatePlan(
            @PathVariable String planId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer durationDays,
            @RequestParam(required = false) Integer maxDevices,
            @RequestParam(required = false) Boolean canDownload,
            @RequestParam(required = false) String videoQuality,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean isActive) {
        MembershipPlan plan = membershipService.updatePlan(
                planId, name, price, durationDays, maxDevices, canDownload, videoQuality, description, isActive);
        return ApiResponseDto.success(plan, "Plan updated successfully");
    }

    @PutMapping("/{memberId}/activate")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate membership")
    public ApiResponseDto<Membership> activateMembership(@PathVariable String memberId) {
        Membership membership = membershipService.activateMembership(memberId);
        return ApiResponseDto.success(membership, "Membership activated successfully");
    }

    @PutMapping("/{memberId}/cancel")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Cancel membership")
    public ApiResponseDto<Void> cancelMembership(@PathVariable String memberId) {
        membershipService.cancelMembership(memberId);
        return ApiResponseDto.success(null, "Membership cancelled successfully");
    }
}
