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
import com.example.backend_pj4.application.port.in.streaming.PlayEpisodeUseCase;
import com.example.backend_pj4.common.annotation.AuthRequired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Episode Playback")
@AuthRequired
@RestController
@RequestMapping("/api/v1/movies/{movieSlug}/episodes")
public class EpisodePlaybackController {

    private final PlayEpisodeUseCase playEpisodeUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public EpisodePlaybackController(
            PlayEpisodeUseCase playEpisodeUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase
    ) {
        this.playEpisodeUseCase = playEpisodeUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @Operation(summary = "Bắt đầu xem tập phim, trả về playback URL và token.")
    @PostMapping("/{episodeNumber}/play")
    public ResponseEntity<PlayContentResult> play(
            @PathVariable String movieSlug,
            @PathVariable int episodeNumber,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        PlayContentResult result = playEpisodeUseCase.execute(userId, movieSlug, episodeNumber);
        return ResponseEntity.ok(result);
    }

    private String resolveUserId(UserDetails userDetails) {
        UserProfileResult user = getCurrentUserUseCase.execute(userDetails.getUsername());
        return user.id();
    }
}
