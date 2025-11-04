package com.example.backend_pj4.application.services.movie;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.entities.Episode;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteEpisodeService extends BaseService {
    private final EpisodeRepository episodeRepository;
    private final MovieRepository movieRepository;

    public void execute(String movieId, String episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new RuntimeException("Episode not found"));

        if (!episode.getMovie().getMovieId().equals(movieId)) {
            throw new RuntimeException("Episode does not belong to this movie");
        }

        episodeRepository.deleteById(episodeId);

        // giảm số tập phim
        movieRepository.findById(movieId).ifPresent(movie -> {
            movie.setTotalEpisodes(movie.getTotalEpisodes() - 1);
            movieRepository.save(movie);
        });
    }
}
