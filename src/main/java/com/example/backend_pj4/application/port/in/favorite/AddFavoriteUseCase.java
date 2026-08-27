package com.example.backend_pj4.application.port.in.favorite;

public interface AddFavoriteUseCase {
    void execute(String userId, String movieSlug);
}
