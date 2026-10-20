package com.example.backend_pj4.application.query.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.dto.comment.MyCommentResult;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.filter.CommentFilter;

class ListMyCommentsQueryHandlerTest {

    private CommentRepository commentRepository;
    private MovieRepository movieRepository;
    private EpisodeRepository episodeRepository;
    private StorageUrlResolver storageUrlResolver;
    private ListMyCommentsQueryHandler handler;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        movieRepository = mock(MovieRepository.class);
        episodeRepository = mock(EpisodeRepository.class);
        storageUrlResolver = mock(StorageUrlResolver.class);
        handler = new ListMyCommentsQueryHandler(commentRepository, movieRepository, episodeRepository, storageUrlResolver);
    }

    @Test
    void execute_traVeDanhSachBinhLuanCuaUser() {
        String userId = "usr_1";
        PageQuery query = new PageQuery(0, 10, "createdAt", SortOrder.desc);

        Comment c1 = Comment.builder()
                .id("cmt_1")
                .userId(userId)
                .movieId("mov_1")
                .episodeId("ep_1")
                .content("Binh luan rat hay")
                .likeCount(5)
                .isApproved(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(commentRepository.find(any(CommentFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(c1), 1L, 1, 10, 1));

        Movie m1 = Movie.builder()
                .id("mov_1")
                .slug("phim-1")
                .title("Phim Mot")
                .posterUrl("posters/m1.jpg")
                .build();
        when(movieRepository.findByIds(anyCollection())).thenReturn(List.of(m1));
        when(storageUrlResolver.resolvePublicImage("posters/m1.jpg")).thenReturn("http://cdn/posters/m1.jpg");

        Episode ep1 = Episode.builder()
                .id("ep_1")
                .episodeNumber(2)
                .title("Tap 2")
                .build();
        when(episodeRepository.findByIds(anyCollection())).thenReturn(List.of(ep1));

        PageResult<MyCommentResult> result = handler.execute(userId, query);

        assertThat(result.items()).hasSize(1);
        MyCommentResult item = result.items().get(0);
        assertThat(item.id()).isEqualTo("cmt_1");
        assertThat(item.content()).isEqualTo("Binh luan rat hay");
        assertThat(item.movieSlug()).isEqualTo("phim-1");
        assertThat(item.movieTitle()).isEqualTo("Phim Mot");
        assertThat(item.posterUrl()).isEqualTo("http://cdn/posters/m1.jpg");
        assertThat(item.episodeNumber()).isEqualTo(2);
        assertThat(item.episodeTitle()).isEqualTo("Tap 2");
        assertThat(item.likeCount()).isEqualTo(5);
        assertThat(item.isApproved()).isTrue();
    }

    @Test
    void execute_khiKhongCoBinhLuan_traVeRong() {
        String userId = "usr_1";
        PageQuery query = new PageQuery(0, 10, "createdAt", SortOrder.desc);

        when(commentRepository.find(any(CommentFilter.class), any(PageQuery.class)))
                .thenReturn(PageResult.empty(query));

        PageResult<MyCommentResult> result = handler.execute(userId, query);

        assertThat(result.items()).isEmpty();
    }
}
