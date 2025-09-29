package com.example.backend_pj4.domain.entities;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ListFavorite {
    private String favoriteId;
    private User user;
    private Movie movie;
    private LocalDateTime addedAt;
}