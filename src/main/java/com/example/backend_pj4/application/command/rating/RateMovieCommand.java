package com.example.backend_pj4.application.command.rating;

public record RateMovieCommand(String userId, String movieSlug, Integer score, String review) {
}
