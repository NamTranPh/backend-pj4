package com.example.backend_pj4.presentation.favorite;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.favorite.FavoriteMovieResult;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.application.port.in.favorite.AddFavoriteUseCase;
import com.example.backend_pj4.application.port.in.favorite.ListFavoriteMoviesUseCase;
import com.example.backend_pj4.application.port.in.favorite.RemoveFavoriteUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Favorites")
@AuthRequired
@RestController
@RequestMapping("/api/v1/me/favorites")
public class FavoriteController {

    private final AddFavoriteUseCase addFavoriteUseCase;
    private final RemoveFavoriteUseCase removeFavoriteUseCase;
    private final ListFavoriteMoviesUseCase listFavoriteMoviesUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public FavoriteController(
            AddFavoriteUseCase addFavoriteUseCase,
            RemoveFavoriteUseCase removeFavoriteUseCase,
            ListFavoriteMoviesUseCase listFavoriteMoviesUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase
    ) {
        this.addFavoriteUseCase = addFavoriteUseCase;
        this.removeFavoriteUseCase = removeFavoriteUseCase;
        this.listFavoriteMoviesUseCase = listFavoriteMoviesUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @Operation(summary = "Thêm phim vào danh sách yêu thích.")
    @PostMapping("/{movieSlug}")
    public ResponseEntity<Void> add(
            @PathVariable String movieSlug,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        addFavoriteUseCase.execute(userId, movieSlug);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Xóa phim khỏi danh sách yêu thích.")
    @DeleteMapping("/{movieSlug}")
    public ResponseEntity<Void> remove(
            @PathVariable String movieSlug,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        removeFavoriteUseCase.execute(userId, movieSlug);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Lấy danh sách phim yêu thích.")
    @GetMapping
    public List<FavoriteMovieResult> list(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = resolveUserId(userDetails);
        return listFavoriteMoviesUseCase.execute(userId);
    }

    private String resolveUserId(UserDetails userDetails) {
        UserProfileResult user = getCurrentUserUseCase.execute(userDetails.getUsername());
        return user.id();
    }
}
