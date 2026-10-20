package com.example.backend_pj4.application.query.comment;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.comment.MyCommentResult;
import com.example.backend_pj4.application.port.in.comment.ListMyCommentsQuery;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.filter.CommentFilter;

@Service
public class ListMyCommentsQueryHandler implements ListMyCommentsQuery {

    private final CommentRepository commentRepository;
    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final StorageUrlResolver storageUrlResolver;

    public ListMyCommentsQueryHandler(CommentRepository commentRepository,
                                      MovieRepository movieRepository,
                                      EpisodeRepository episodeRepository,
                                      StorageUrlResolver storageUrlResolver) {
        this.commentRepository = commentRepository;
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.storageUrlResolver = storageUrlResolver;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<MyCommentResult> execute(String userId, PageQuery query) {
        PageResult<Comment> page = commentRepository.find(
                new CommentFilter(null, null, false, false, null, null, userId), query);

        if (page.items().isEmpty()) {
            return page.withItems(List.of());
        }

        List<String> movieIds = page.items().stream()
                .map(Comment::getMovieId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<String, Movie> moviesById = movieRepository.findByIds(movieIds).stream()
                .collect(Collectors.toMap(Movie::getId, Function.identity()));

        List<String> episodeIds = page.items().stream()
                .map(Comment::getEpisodeId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<String, Episode> episodesById = episodeIds.isEmpty()
                ? Map.of()
                : episodeRepository.findByIds(episodeIds).stream()
                        .collect(Collectors.toMap(Episode::getId, Function.identity()));

        List<MyCommentResult> results = page.items().stream()
                .map(c -> {
                    Movie movie = moviesById.get(c.getMovieId());
                    String movieSlug = movie != null ? movie.getSlug() : null;
                    String movieTitle = movie != null ? movie.getTitle() : null;
                    String posterUrl = movie != null ? storageUrlResolver.resolvePublicImage(movie.getPosterUrl()) : null;

                    Episode episode = c.getEpisodeId() != null ? episodesById.get(c.getEpisodeId()) : null;
                    Integer episodeNumber = episode != null ? episode.getEpisodeNumber() : null;
                    String episodeTitle = episode != null ? episode.getTitle() : null;

                    return new MyCommentResult(
                            c.getId(),
                            c.getContent(),
                            c.getMovieId(),
                            movieSlug,
                            movieTitle,
                            posterUrl,
                            c.getEpisodeId(),
                            episodeNumber,
                            episodeTitle,
                            c.getParentId(),
                            c.getLikeCount(),
                            Boolean.TRUE.equals(c.getIsApproved()),
                            c.getCreatedAt(),
                            c.getUpdatedAt()
                    );
                })
                .toList();

        return page.withItems(results);
    }
}
