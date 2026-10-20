package com.example.backend_pj4.application.port.in.rating;

public interface DeleteRatingUseCase {
    void execute(String userId, String movieSlug);
}
