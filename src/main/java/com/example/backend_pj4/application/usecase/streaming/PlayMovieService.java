package com.example.backend_pj4.application.usecase.streaming;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.streaming.PlayContentResult;
import com.example.backend_pj4.application.port.in.streaming.PlayMovieUseCase;
import com.example.backend_pj4.application.port.out.PlaybackTokenService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.HistoryWatchingRepository;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class PlayMovieService implements PlayMovieUseCase {

    private final MovieRepository movieRepository;
    private final MembershipRepository membershipRepository;
    private final HistoryWatchingRepository historyWatchingRepository;
    private final PlaybackTokenService playbackTokenService;

    public PlayMovieService(
            MovieRepository movieRepository,
            MembershipRepository membershipRepository,
            HistoryWatchingRepository historyWatchingRepository,
            PlaybackTokenService playbackTokenService
    ) {
        this.movieRepository = movieRepository;
        this.membershipRepository = membershipRepository;
        this.historyWatchingRepository = historyWatchingRepository;
        this.playbackTokenService = playbackTokenService;
    }

    @Override
    @Transactional(readOnly = true)
    public PlayContentResult execute(String userId, String movieSlug) {
        Movie movie = movieRepository.findBySlug(movieSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        if (movie.getMovieType() != MovieType.SINGLE) {
            throw new CustomException(ErrorCode.MOVIE_NOT_SINGLE);
        }

        if (movie.getStatus() != VideoStatus.READY || movie.getMasterPlaylistKey() == null) {
            throw new CustomException(ErrorCode.MOVIE_NOT_READY);
        }

        if (Boolean.TRUE.equals(movie.getIsPremium())) {
            var membership = membershipRepository.findActiveByUserId(userId);
            if (membership.isEmpty()) {
                throw new CustomException(ErrorCode.MEMBERSHIP_REQUIRED);
            }
            var m = membership.get();
            if (!Boolean.TRUE.equals(m.getIsActive()) || m.getEndDate().isBefore(java.time.LocalDate.now())) {
                throw new CustomException(ErrorCode.MEMBERSHIP_EXPIRED);
            }
        }

        Integer resumePosition = historyWatchingRepository
                .findByUserIdAndMovieId(userId, movie.getId())
                .map(h -> Boolean.TRUE.equals(h.getIsCompleted()) ? 0 : h.getLastPosition())
                .orElse(0);

        String token = playbackTokenService.issueToken(movie.getId(), "MOVIE", userId);
        String playbackUrl = "/api/v1/stream/" + movie.getId() + "/master.m3u8?token=" + token;

        return new PlayContentResult(movie.getId(), "MOVIE", playbackUrl, token, resumePosition);
    }
}
