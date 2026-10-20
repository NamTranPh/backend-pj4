package com.example.backend_pj4.application.query.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.dto.comment.CommentResult;
import com.example.backend_pj4.application.mapper.CommentResultMapper;
import com.example.backend_pj4.application.port.out.CommentLikeCounter;
import com.example.backend_pj4.application.port.out.CommentListCache;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.domain.repository.filter.CommentFilter;

/**
 * Khoá hai tính chất của luồng đọc bình luận:
 *
 * <ul>
 *   <li><b>Số truy vấn không phụ thuộc độ sâu cây.</b> Đây là lý do tồn tại của cột
 *       {@code root_id}; ai đó đổi sang dò theo {@code parent_id} từng cấp sẽ làm một luồng sâu
 *       10 cấp thành 11 truy vấn mà không có gì báo.</li>
 *   <li><b>Redis hỏng không được làm hỏng việc xem bình luận</b> (AGENTS.md §18).</li>
 * </ul>
 */
class ListCommentsQueryHandlerTest {

    private static final String MOVIE_ID = "mov_1";
    private static final String MOVIE_SLUG = "phim-hay";
    private static final String VIEWER = "usr_viewer";
    private static final PageQuery QUERY = new PageQuery(0, 15, "createdAt", SortOrder.desc);

    private MovieRepository movieRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private CommentListCache commentListCache;
    private CommentLikeCounter commentLikeCounter;
    private ListCommentsQueryHandler handler;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        commentRepository = mock(CommentRepository.class);
        userRepository = mock(UserRepository.class);
        commentListCache = mock(CommentListCache.class);
        commentLikeCounter = mock(CommentLikeCounter.class);

        handler = new ListCommentsQueryHandler(movieRepository, commentRepository, userRepository,
                new CommentResultMapper(mock(StorageUrlResolver.class)),
                commentListCache, commentLikeCounter);

        when(movieRepository.findBySlug(MOVIE_SLUG))
                .thenReturn(Optional.of(Movie.builder().id(MOVIE_ID).slug(MOVIE_SLUG).build()));
        when(commentListCache.get(any(CommentFilter.class), any(PageQuery.class)))
                .thenReturn(Optional.empty());
        when(userRepository.findByIds(anyCollection()))
                .thenReturn(List.of(User.builder().id("usr_a").name("A").build()));
        when(commentLikeCounter.deltasOf(anyCollection())).thenReturn(Map.of());
        when(commentRepository.findLikedIdsIn(anyString(), anyCollection())).thenReturn(List.of());
    }

    private Comment comment(String id, String parentId, String rootId) {
        return Comment.builder()
                .id(id).userId("usr_a").movieId(MOVIE_ID)
                .parentId(parentId).rootId(rootId)
                .content("noi dung " + id).isApproved(true).likeCount(0)
                .build();
    }

    /** Cây 4 cấp: root -> c2 -> c3 -> c4, tất cả cùng rootId. */
    private void givenDeepThread() {
        when(commentRepository.find(any(CommentFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(comment("root", null, null)), 1, 1, 15, 1));
        when(commentRepository.findByRootIds(anyCollection())).thenReturn(List.of(
                comment("c2", "root", "root"),
                comment("c3", "c2", "root"),
                comment("c4", "c3", "root")));
    }

    @Test
    void capNhieuTang_van_chi_HAI_truyVan_cho_cayBinhLuan() {
        givenDeepThread();

        handler.execute(MOVIE_SLUG, null, QUERY, null);

        // Một câu lấy trang gốc, một câu lấy toàn bộ con cháu. Không có câu thứ ba cho dù cây
        // sâu bao nhiêu cấp.
        verify(commentRepository, times(1)).find(any(CommentFilter.class), any(PageQuery.class));
        verify(commentRepository, times(1)).findByRootIds(anyCollection());
    }

    @Test
    void long_dung_bon_cap_theo_parentId() {
        givenDeepThread();

        PageResult<CommentResult> page = handler.execute(MOVIE_SLUG, null, QUERY, null);

        CommentResult root = page.items().get(0);
        assertThat(root.id()).isEqualTo("root");
        assertThat(root.replies()).hasSize(1);
        assertThat(root.replies().get(0).id()).isEqualTo("c2");
        assertThat(root.replies().get(0).replies().get(0).id()).isEqualTo("c3");
        assertThat(root.replies().get(0).replies().get(0).replies().get(0).id()).isEqualTo("c4");
    }

    @Test
    void binhLuanDaBiAn_va_ca_nhanh_con_deu_khong_hien() {
        when(commentRepository.find(any(CommentFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(comment("root", null, null)), 1, 1, 15, 1));
        // c2 bị ẩn; c3 là con của c2 nên thành mồ côi và cũng không hiện ra.
        Comment hidden = comment("c2", "root", "root").toBuilder().isApproved(false).build();
        when(commentRepository.findByRootIds(anyCollection()))
                .thenReturn(List.of(hidden, comment("c3", "c2", "root")));

        PageResult<CommentResult> page = handler.execute(MOVIE_SLUG, null, QUERY, null);

        assertThat(page.items().get(0).replies()).isEmpty();
    }

    @Test
    void goiKhongToken_khong_tra_cuu_trangThaiDaThich() {
        givenDeepThread();

        PageResult<CommentResult> page = handler.execute(MOVIE_SLUG, null, QUERY, null);

        assertThat(page.items().get(0).isLiked()).isFalse();
        verify(commentRepository, never()).findLikedIdsIn(anyString(), anyCollection());
    }

    @Test
    void coToken_bat_co_daThich_cho_dung_binhLuan() {
        givenDeepThread();
        when(commentRepository.findLikedIdsIn(anyString(), anyCollection()))
                .thenReturn(List.of("c3"));

        PageResult<CommentResult> page = handler.execute(MOVIE_SLUG, null, QUERY, VIEWER);

        CommentResult root = page.items().get(0);
        assertThat(root.isLiked()).isFalse();
        assertThat(root.replies().get(0).replies().get(0).isLiked()).isTrue();
    }

    @Test
    void soLuotThich_PHAI_cong_phan_chenhLech_dang_nam_o_Redis() {
        givenDeepThread();
        when(commentLikeCounter.deltasOf(anyCollection())).thenReturn(Map.of("root", 7L));

        PageResult<CommentResult> page = handler.execute(MOVIE_SLUG, null, QUERY, null);

        assertThat(page.items().get(0).likeCount()).isEqualTo(7);
    }

    @Test
    void cacheTrung_KHONG_cham_vao_database() {
        PageResult<CommentResult> cached = new PageResult<>(
                List.of(new CommentResult("root", "x", null, null, 0, false, null, null, List.of())),
                1, 1, 15, 1);
        when(commentListCache.get(any(CommentFilter.class), any(PageQuery.class)))
                .thenReturn(Optional.of(cached));

        handler.execute(MOVIE_SLUG, null, QUERY, null);

        verify(commentRepository, never()).find(any(CommentFilter.class), any(PageQuery.class));
        verify(commentRepository, never()).findByRootIds(anyCollection());
    }

    @Test
    void cacheTruot_PHAI_nap_lai_cache() {
        givenDeepThread();

        handler.execute(MOVIE_SLUG, null, QUERY, null);

        verify(commentListCache).put(any(CommentFilter.class), any(PageQuery.class), any());
    }

    @Test
    void endpointCongKhai_PHAI_chi_lay_binhLuanGoc_da_duyet() {
        givenDeepThread();

        handler.execute(MOVIE_SLUG, "ep_1", QUERY, null);

        // Ba cờ an toàn này quyết định endpoint công khai có lộ bình luận admin vừa ẩn hay không.
        verify(commentRepository).find(
                org.mockito.ArgumentMatchers.eq(
                        new CommentFilter(MOVIE_ID, "ep_1", true, true, Boolean.TRUE, null)),
                any(PageQuery.class));
    }
}
