package com.example.backend_pj4.application.query.episode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.episode.PublicEpisodeResult;
import com.example.backend_pj4.application.port.in.episode.ListPublicEpisodesQuery;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.filter.EpisodeFilter;

@Service
public class ListPublicEpisodesQueryHandler implements ListPublicEpisodesQuery {

    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final StorageUrlResolver storageUrlResolver;

    public ListPublicEpisodesQueryHandler(MovieRepository movieRepository,
                                          EpisodeRepository episodeRepository,
                                          StorageUrlResolver storageUrlResolver) {
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.storageUrlResolver = storageUrlResolver;
    }

    /**
     * Transaction ở đây không phải cho đủ lệ: hai lần gọi repository phải nằm trong cùng một
     * transaction, nếu không thì giữa lúc tìm được phim và lúc lấy tập phim, phim có thể bị xoá.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<PublicEpisodeResult> execute(String movieSlug, String search, PageQuery query) {
        Movie movie = movieRepository.findBySlug(movieSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        return episodeRepository.find(new EpisodeFilter(movie.getId(), search), query)
                .map(this::toResult);
    }

    private PublicEpisodeResult toResult(Episode episode) {
        return new PublicEpisodeResult(
                episode.getEpisodeNumber(),
                episode.getTitle(),
                episode.getDescription(),
                episode.getDuration(),
                storageUrlResolver.resolvePublicImage(episode.getThumbnailUrl()),
                episode.getStatus(),
                episode.getAirDate());
    }
}
