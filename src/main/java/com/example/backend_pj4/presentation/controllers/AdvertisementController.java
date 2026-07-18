package com.example.backend_pj4.presentation.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.services.advertisement.AdvertisementService;
import com.example.backend_pj4.common.base.BaseController;
import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.common.enums.AdType;
import com.example.backend_pj4.domain.entities.Advertisement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/advertisements")
@RequiredArgsConstructor
@Tag(name = "Advertisements")
public class AdvertisementController extends BaseController {

    private final AdvertisementService advertisementService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create advertisement")
    public ApiResponseDto<Advertisement> createAdvertisement(
            @RequestParam String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String clickUrl,
            @RequestParam AdType adType,
            @RequestParam(required = false) String position,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String createdBy) {
        Advertisement ad = advertisementService.createAdvertisement(
                title, content, imageUrl, clickUrl, adType, position, startDate, endDate, createdBy);
        return ApiResponseDto.success(ad, "Advertisement created successfully");
    }

    @PutMapping("/{adId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update advertisement")
    public ApiResponseDto<Advertisement> updateAdvertisement(
            @PathVariable String adId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String clickUrl,
            @RequestParam(required = false) AdType adType,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Boolean isActive) {
        Advertisement ad = advertisementService.updateAdvertisement(
                adId, title, content, imageUrl, clickUrl, adType, position, startDate, endDate, isActive);
        return ApiResponseDto.success(ad, "Advertisement updated successfully");
    }

    @GetMapping("/active")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get all active advertisements")
    public ApiResponseDto<List<Advertisement>> getActiveAds() {
        List<Advertisement> ads = advertisementService.getActiveAds();
        return ApiResponseDto.success(ads, "Active advertisements retrieved");
    }

    @GetMapping("/active/type/{adType}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get active ads by type")
    public ApiResponseDto<List<Advertisement>> getActiveAdsByType(@PathVariable AdType adType) {
        List<Advertisement> ads = advertisementService.getActiveAdsByType(adType.name());
        return ApiResponseDto.success(ads, "Active ads by type retrieved");
    }

    @GetMapping("/active/position/{position}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get active ads by position")
    public ApiResponseDto<List<Advertisement>> getActiveAdsByPosition(@PathVariable String position) {
        List<Advertisement> ads = advertisementService.getActiveAdsByPosition(position);
        return ApiResponseDto.success(ads, "Active ads by position retrieved");
    }

    @PostMapping("/{adId}/view")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Record ad view")
    public ApiResponseDto<Void> recordView(@PathVariable String adId) {
        advertisementService.recordView(adId);
        return ApiResponseDto.success(null, "View recorded");
    }

    @PostMapping("/{adId}/click")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Record ad click")
    public ApiResponseDto<Void> recordClick(@PathVariable String adId) {
        advertisementService.recordClick(adId);
        return ApiResponseDto.success(null, "Click recorded");
    }

    @DeleteMapping("/{adId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete advertisement")
    public ApiResponseDto<Void> deleteAdvertisement(@PathVariable String adId) {
        advertisementService.deleteAdvertisement(adId);
        return ApiResponseDto.success(null, "Advertisement deleted successfully");
    }

    @GetMapping("/stats/top-clicked")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get top clicked ads")
    public ApiResponseDto<List<Advertisement>> getTopClickedAds(
            @RequestParam(defaultValue = "10") int limit) {
        List<Advertisement> ads = advertisementService.getTopClickedAds(limit);
        return ApiResponseDto.success(ads, "Top clicked ads retrieved");
    }

    @GetMapping("/stats/top-viewed")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get top viewed ads")
    public ApiResponseDto<List<Advertisement>> getTopViewedAds(
            @RequestParam(defaultValue = "10") int limit) {
        List<Advertisement> ads = advertisementService.getTopViewedAds(limit);
        return ApiResponseDto.success(ads, "Top viewed ads retrieved");
    }
}
