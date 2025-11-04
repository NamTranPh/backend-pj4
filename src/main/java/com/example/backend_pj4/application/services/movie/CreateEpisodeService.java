package com.example.backend_pj4.application.services.movie;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.request.episode.RequestCreateEpisodeDto;
import com.example.backend_pj4.common.enums.MovieType;
import com.example.backend_pj4.domain.entities.Episode;
import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateEpisodeService {
    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;

    public Episode execute(String movieId, RequestCreateEpisodeDto dto) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        if (movie.getMovieType() == MovieType.SINGLE) {
            throw new RuntimeException("Không thể thêm tập cho phim lẻ");
        }

        Episode episode = Episode.builder()
                .episodeNumber(dto.getEpisodeNumber())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .duration(dto.getDuration())
                .videoUrl(dto.getVideoUrl())
                .thumbnailUrl(dto.getThumbnailUrl())
                .airDate(dto.getAirDate())
                .isPremium(dto.getIsPremium())
                .isActive(dto.getIsActive())
                .movie(movie)
                .build();

        Episode saved = episodeRepository.save(episode);

        // tăng tổng số tập
        movie.setTotalEpisodes((movie.getTotalEpisodes() == null ? 0 : movie.getTotalEpisodes()) + 1);
        movieRepository.save(movie);

        return saved;
    }
}
