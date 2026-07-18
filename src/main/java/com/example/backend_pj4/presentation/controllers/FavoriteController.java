package com.example.backend_pj4.presentation.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.services.favorite.FavoriteService;
import com.example.backend_pj4.common.base.BaseController;
import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.domain.entities.ListFavorite;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites")
public class FavoriteController extends BaseController {

    private final FavoriteService favoriteService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Add movie to favorites")
    public ApiResponseDto<ListFavorite> addToFavorites(
            @RequestParam String userId,
            @RequestParam String movieId) {
        ListFavorite favorite = favoriteService.addToFavorite(userId, movieId);
        return ApiResponseDto.success(favorite, "Movie added to favorites");
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Remove movie from favorites")
    public ApiResponseDto<Void> removeFromFavorites(
            @RequestParam String userId,
            @RequestParam String movieId) {
        favoriteService.removeFromFavorite(userId, movieId);
        return ApiResponseDto.success(null, "Movie removed from favorites");
    }

    @GetMapping("/user/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get user favorites")
    public ApiResponseDto<List<ListFavorite>> getUserFavorites(@PathVariable String userId) {
        List<ListFavorite> favorites = favoriteService.getUserFavorites(userId);
        return ApiResponseDto.success(favorites, "Favorites retrieved successfully");
    }

    @GetMapping("/user/{userId}/check/{movieId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Check if movie is in favorites")
    public ApiResponseDto<Boolean> isFavorite(
            @PathVariable String userId,
            @PathVariable String movieId) {
        boolean isFav = favoriteService.isFavorite(userId, movieId);
        return ApiResponseDto.success(isFav, isFav ? "Movie is in favorites" : "Movie is not in favorites");
    }

    @DeleteMapping("/user/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Clear all user favorites")
    public ApiResponseDto<Void> clearUserFavorites(@PathVariable String userId) {
        favoriteService.clearUserFavorites(userId);
        return ApiResponseDto.success(null, "All favorites cleared");
    }
}
