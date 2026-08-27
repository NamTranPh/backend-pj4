package com.example.backend_pj4.application.usecase.streaming;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.streaming.PlayContentResult;
import com.example.backend_pj4.application.port.in.streaming.PlayEpisodeUseCase;
import com.example.backend_pj4.application.port.out.PlaybackTokenService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.VideoStatus;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.HistoryWatchingRepository;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class PlayEpisodeService implements PlayEpisodeUseCase {

    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final MembershipRepository membershipRepository;
    private final HistoryWatchingRepository historyWatchingRepository;
    private final PlaybackTokenService playbackTokenService;

    public PlayEpisodeService(
            MovieRepository movieRepository,
            EpisodeRepository episodeRepository,
            MembershipRepository membershipRepository,
            HistoryWatchingRepository historyWatchingRepository,
            PlaybackTokenService playbackTokenService
    ) {
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.membershipRepository = membershipRepository;
        this.historyWatchingRepository = historyWatchingRepository;
        this.playbackTokenService = playbackTokenService;
    }

    @Override
    @Transactional(readOnly = true)
    public PlayContentResult execute(String userId, String movieSlug, int episodeNumber) {
        Movie movie = movieRepository.findBySlug(movieSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        Episode episode = episodeRepository.findByMovieIdAndEpisodeNumber(movie.getId(), episodeNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.EPISODE_NOT_FOUND));

        if (episode.getStatus() != VideoStatus.READY || episode.getMasterPlaylistKey() == null) {
            throw new CustomException(ErrorCode.EPISODE_NOT_READY);
        }

        if (Boolean.TRUE.equals(movie.getIsPremium()) || Boolean.TRUE.equals(episode.getIsPremium())) {
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
                .findByUserIdAndMovieIdAndEpisodeId(userId, movie.getId(), episode.getId())
                .map(h -> Boolean.TRUE.equals(h.getIsCompleted()) ? 0 : h.getLastPosition())
                .orElse(0);

        String token = playbackTokenService.issueToken(episode.getId(), "EPISODE", userId);
        String playbackUrl = "/api/v1/stream/" + episode.getId() + "/master.m3u8?token=" + token;

        return new PlayContentResult(episode.getId(), "EPISODE", playbackUrl, token, resumePosition);
    }
}
