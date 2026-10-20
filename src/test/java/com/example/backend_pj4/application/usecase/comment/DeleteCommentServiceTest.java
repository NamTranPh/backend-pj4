package com.example.backend_pj4.application.usecase.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.backend_pj4.application.port.out.CommentListCache;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.repository.CommentRepository;

/**
 * Xoá một bình luận phải kéo theo <b>cả nhánh con</b>.
 *
 * <p>Chỉ xoá đúng nút đó thì nhánh con biến mất khỏi màn hình (cây dựng từ gốc trở xuống) nhưng
 * vẫn nằm nguyên trong database — người dùng tưởng đã xoá hết, thực ra chưa. Lỗi kiểu này không
 * ai phát hiện ra cho tới khi có người đọc thẳng database.
 */
class DeleteCommentServiceTest {

    private static final String OWNER = "usr_owner";
    private static final String OTHER = "usr_other";
    private static final String MOVIE_ID = "mov_1";

    private CommentRepository commentRepository;
    private CommentListCache commentListCache;
    private DeleteCommentService service;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        commentListCache = mock(CommentListCache.class);
        service = new DeleteCommentService(commentRepository, commentListCache);
    }

    private Comment comment(String id, String parentId, String rootId, String userId) {
        return Comment.builder()
                .id(id).userId(userId).movieId(MOVIE_ID)
                .parentId(parentId).rootId(rootId).isApproved(true).build();
    }

    /** root -> c2 -> c3, và một nhánh riêng root -> c4 không liên quan tới c2. */
    private void givenThread() {
        when(commentRepository.findByRootIds(anyCollection())).thenReturn(List.of(
                comment("root", null, null, OWNER),
                comment("c2", "root", "root", OTHER),
                comment("c3", "c2", "root", OWNER),
                comment("c4", "root", "root", OTHER)));
    }

    @SuppressWarnings("unchecked")
    private Collection<String> deletedIds() {
        ArgumentCaptor<Collection<String>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(commentRepository).softDeleteByIds(captor.capture());
        return captor.getValue();
    }

    @Test
    void xoaBinhLuanGoc_PHAI_xoa_het_ca_cay() {
        when(commentRepository.findById("root"))
                .thenReturn(Optional.of(comment("root", null, null, OWNER)));
        givenThread();

        service.execute("root", OWNER, false);

        assertThat(deletedIds()).containsExactlyInAnyOrder("root", "c2", "c3", "c4");
    }

    @Test
    void xoaBinhLuanGiuaCay_chi_xoa_nhanh_cua_no() {
        when(commentRepository.findById("c2"))
                .thenReturn(Optional.of(comment("c2", "root", "root", OWNER)));
        givenThread();

        service.execute("c2", OWNER, false);

        // c4 là nhánh khác của cùng một luồng, không được đụng tới.
        assertThat(deletedIds()).containsExactlyInAnyOrder("c2", "c3");
    }

    @Test
    void nguoiKhac_KHONG_xoa_duoc_binhLuan_cua_nguoiTa() {
        when(commentRepository.findById("root"))
                .thenReturn(Optional.of(comment("root", null, null, OWNER)));

        assertThatThrownBy(() -> service.execute("root", OTHER, false))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMENT_NOT_OWNED);

        verify(commentRepository, never()).softDeleteByIds(any());
    }

    @Test
    void admin_xoa_duoc_binhLuan_cua_nguoiKhac() {
        when(commentRepository.findById("root"))
                .thenReturn(Optional.of(comment("root", null, null, OWNER)));
        givenThread();

        service.execute("root", OTHER, true);

        assertThat(deletedIds()).contains("root");
    }

    @Test
    void xoaXong_PHAI_vo_hieu_hoa_cache() {
        when(commentRepository.findById("root"))
                .thenReturn(Optional.of(comment("root", null, null, OWNER)));
        givenThread();

        service.execute("root", OWNER, false);

        verify(commentListCache).bumpVersion(MOVIE_ID);
    }

    @Test
    void binhLuanKhongTonTai_bao_loi_khongTimThay() {
        when(commentRepository.findById("khong-co")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("khong-co", OWNER, false))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMENT_NOT_FOUND);
    }
}
