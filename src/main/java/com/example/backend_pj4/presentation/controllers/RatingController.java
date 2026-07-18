package com.example.backend_pj4.presentation.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.services.rating.RatingService;
import com.example.backend_pj4.common.base.BaseController;
import com.example.backend_pj4.common.dto.response.ApiResponseDto;
import com.example.backend_pj4.domain.entities.Rating;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings")
public class RatingController extends BaseController {

    private final RatingService ratingService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Rate a movie", description = "Rate a movie with a score (1-10)")
    public ApiResponseDto<Rating> rateMovie(
            @RequestParam String userId,
            @RequestParam String movieId,
            @RequestParam Integer score,
            @RequestParam(required = false) String review) {
        Rating rating = ratingService.rateMovie(userId, movieId, score, review);
        return ApiResponseDto.success(rating, "Movie rated successfully");
    }

    @GetMapping("/movie/{movieId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get ratings by movie")
    public ApiResponseDto<List<Rating>> getRatingsByMovie(@PathVariable String movieId) {
        List<Rating> ratings = ratingService.getRatingsByMovie(movieId);
        return ApiResponseDto.success(ratings, "Ratings retrieved successfully");
    }

    @GetMapping("/movie/{movieId}/average")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get average rating of a movie")
    public ApiResponseDto<Map<String, Object>> getAverageRating(@PathVariable String movieId) {
        Double average = ratingService.getAverageRating(movieId);
        long count = ratingService.countRatings(movieId);
        return ApiResponseDto.success(Map.of("averageRating", average, "ratingCount", count), "Average rating retrieved");
    }

    @GetMapping("/user/{userId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get user ratings")
    public ApiResponseDto<List<Rating>> getUserRatings(@PathVariable String userId) {
        List<Rating> ratings = ratingService.getUserRatings(userId);
        return ApiResponseDto.success(ratings, "User ratings retrieved successfully");
    }

    @GetMapping("/user/{userId}/movie/{movieId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get user's rating for a movie")
    public ApiResponseDto<Rating> getUserMovieRating(
            @PathVariable String userId,
            @PathVariable String movieId) {
        return ratingService.getUserRating(userId, movieId)
                .map(r -> ApiResponseDto.success(r, "Rating found"))
                .orElse(ApiResponseDto.success(null, "No rating found"));
    }

    @GetMapping("/top-rated")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get top rated movies")
    public ApiResponseDto<List<Object[]>> getTopRatedMovies(@RequestParam(defaultValue = "10") int limit) {
        List<Object[]> topRated = ratingService.getTopRatedMovies(limit);
        return ApiResponseDto.success(topRated, "Top rated movies retrieved");
    }

    @DeleteMapping("/user/{userId}/movie/{movieId}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Delete user's rating for a movie")
    public ApiResponseDto<Void> deleteUserMovieRating(
            @PathVariable String userId,
            @PathVariable String movieId) {
        ratingService.deleteUserMovieRating(userId, movieId);
        return ApiResponseDto.success(null, "Rating deleted successfully");
    }
}
