package com.example.backend_pj4.presentation.streaming;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.streaming.PlayContentResult;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.application.port.in.streaming.PlayMovieUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Movie Playback")
@RestController
@RequestMapping("/api/v1/movies")
public class MoviePlaybackController {

    private final PlayMovieUseCase playMovieUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public MoviePlaybackController(
            PlayMovieUseCase playMovieUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase
    ) {
        this.playMovieUseCase = playMovieUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @Operation(summary = "Bắt đầu xem phim lẻ, trả về playback URL và token.")
    @AuthRequired
    @PostMapping("/{movieSlug}/play")
    public ResponseEntity<PlayContentResult> play(
            @PathVariable String movieSlug,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserProfileResult user = getCurrentUserUseCase.execute(userDetails.getUsername());
        PlayContentResult result = playMovieUseCase.execute(user.id(), movieSlug);
        return ResponseEntity.ok(result);
    }
}
