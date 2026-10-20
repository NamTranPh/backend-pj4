package com.example.backend_pj4.application.usecase.comment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.port.out.CommentLikeCounter;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.repository.CommentRepository;

/**
 * Khoá hai điều:
 *
 * <ul>
 *   <li><b>Lượt thích luôn ghi vào MySQL</b>, kể cả khi bộ đếm Redis hoạt động — bảng
 *       {@code comment_like} là thứ trả lời "ai đã thích cái gì", mất nó là tải lại trang mất
 *       tim đỏ và bấm thích được nhiều lần.</li>
 *   <li><b>Redis hỏng thì cộng thẳng vào database</b>, chứ không để con số đứng yên ngay trước
 *       mắt người vừa bấm.</li>
 * </ul>
 */
class LikeCommentServiceTest {

    private static final String USER = "usr_1";
    private static final String COMMENT_ID = "cmt_1";

    private CommentRepository commentRepository;
    private CommentLikeCounter commentLikeCounter;
    private LikeCommentService service;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        commentLikeCounter = mock(CommentLikeCounter.class);
        service = new LikeCommentService(commentRepository, commentLikeCounter);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(
                Comment.builder().id(COMMENT_ID).userId("usr_khac").movieId("mov_1").build()));
        when(commentLikeCounter.tryIncrement(anyString(), anyInt())).thenReturn(true);
    }

    @Test
    void thich_ghi_dong_vao_MySQL_va_tang_bo_dem_Redis() {
        when(commentRepository.existsLike(USER, COMMENT_ID)).thenReturn(false);

        service.execute(COMMENT_ID, USER, true);

        verify(commentRepository).addLike(USER, COMMENT_ID);
        verify(commentLikeCounter).tryIncrement(COMMENT_ID, 1);
        // Đường bình thường không đụng vào cột like_count: đó là việc của job gộp.
        verify(commentRepository, never()).addLikeCount(anyString(), anyInt());
    }

    @Test
    void thichHaiLan_bao_loi_va_KHONG_tang_bo_dem() {
        when(commentRepository.existsLike(USER, COMMENT_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.execute(COMMENT_ID, USER, true))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMENT_ALREADY_LIKED);

        verify(commentRepository, never()).addLike(anyString(), anyString());
        verify(commentLikeCounter, never()).tryIncrement(anyString(), anyInt());
    }

    @Test
    void boThich_tru_bo_dem() {
        when(commentRepository.existsLike(USER, COMMENT_ID)).thenReturn(true);

        service.execute(COMMENT_ID, USER, false);

        verify(commentRepository).removeLike(USER, COMMENT_ID);
        verify(commentLikeCounter).tryIncrement(COMMENT_ID, -1);
    }

    @Test
    void boThich_thu_chuaTungThich_bao_loi() {
        when(commentRepository.existsLike(USER, COMMENT_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.execute(COMMENT_ID, USER, false))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMENT_LIKE_NOT_FOUND);

        verify(commentRepository, never()).removeLike(anyString(), anyString());
    }

    @Test
    void RedisHong_PHAI_cong_thang_vao_database() {
        when(commentRepository.existsLike(USER, COMMENT_ID)).thenReturn(false);
        when(commentLikeCounter.tryIncrement(anyString(), anyInt())).thenReturn(false);

        service.execute(COMMENT_ID, USER, true);

        // Lượt thích vẫn vào comment_like, và con số vẫn nhúc nhích — chỉ là chậm hơn.
        verify(commentRepository).addLike(USER, COMMENT_ID);
        verify(commentRepository).addLikeCount(COMMENT_ID, 1);
    }

    @Test
    void thichBinhLuanDaXoa_bao_khongTimThay() {
        when(commentRepository.findById("da-xoa")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute("da-xoa", USER, true))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMENT_NOT_FOUND);

        verify(commentRepository, never()).addLike(anyString(), anyString());
    }

    @Test
    void thichBinhLuanCuaChinhMinh_duoc_phep() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(
                Comment.builder().id(COMMENT_ID).userId(USER).movieId("mov_1").build()));
        when(commentRepository.existsLike(USER, COMMENT_ID)).thenReturn(false);

        service.execute(COMMENT_ID, USER, true);

        verify(commentRepository).addLike(USER, COMMENT_ID);
    }
}
