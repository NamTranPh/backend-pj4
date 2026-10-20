package com.example.backend_pj4.application.usecase.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.backend_pj4.application.port.out.CommentLikeCounter;
import com.example.backend_pj4.domain.repository.CommentRepository;

/**
 * Job gộp phải <b>đếm lại</b> từ bảng {@code comment_like}, không cộng phần chênh lệch của Redis
 * vào cột cũ.
 *
 * <p>Đây là thứ làm cho con số tự chữa lành: Redis restart mất phần chênh lệch thì lần gộp kế
 * tiếp vẫn ra đúng. Nếu đổi sang cộng dồn, mỗi lần mất mát thành một sai lệch vĩnh viễn và chỉ
 * phát hiện được khi có người ngồi đếm tay.
 */
class FlushLikeCountsServiceTest {

    private CommentLikeCounter commentLikeCounter;
    private CommentRepository commentRepository;
    private FlushLikeCountsService service;

    @BeforeEach
    void setUp() {
        commentLikeCounter = mock(CommentLikeCounter.class);
        commentRepository = mock(CommentRepository.class);
        service = new FlushLikeCountsService(commentLikeCounter, commentRepository);
    }

    @SuppressWarnings("unchecked")
    @Test
    void gop_PHAI_demLai_dung_tap_binhLuan_vua_doi() {
        when(commentLikeCounter.drainPendingDeltas())
                .thenReturn(Map.of("cmt_1", 3L, "cmt_2", -1L));

        int updated = service.execute();

        ArgumentCaptor<Collection<String>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(commentRepository).recountLikeCounts(captor.capture());
        assertThat(captor.getValue()).containsExactlyInAnyOrder("cmt_1", "cmt_2");
        assertThat(updated).isEqualTo(2);
    }

    @Test
    void khongCoGiDeGop_KHONG_cham_vao_database() {
        when(commentLikeCounter.drainPendingDeltas()).thenReturn(Map.of());

        int updated = service.execute();

        assertThat(updated).isZero();
        verify(commentRepository, never()).recountLikeCounts(any());
    }

    @Test
    void gop_KHONG_duoc_cong_don_delta_vao_cot_cu() {
        when(commentLikeCounter.drainPendingDeltas()).thenReturn(Map.of("cmt_1", 3L));

        service.execute();

        // addLikeCount là đường cộng dồn, chỉ dành cho lúc Redis hỏng. Job gộp không được dùng.
        verify(commentRepository, never()).addLikeCount(any(), org.mockito.ArgumentMatchers.anyInt());
    }
}
