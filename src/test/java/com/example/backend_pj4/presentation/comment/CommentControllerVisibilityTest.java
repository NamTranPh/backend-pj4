package com.example.backend_pj4.presentation.comment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.port.in.comment.ListCommentsAdminQuery;
import com.example.backend_pj4.application.port.in.comment.ListCommentsQuery;
import com.example.backend_pj4.application.port.in.comment.SetCommentApprovalUseCase;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

/**
 * Khoá ranh giới giữa endpoint bình luận công khai và endpoint kiểm duyệt của admin.
 *
 * <p>Hai nhánh này dùng chung một bảng và gần như chung một câu truy vấn, khác nhau ở mấy cờ
 * boolean. Sai một cờ thì <b>không có lỗi biên dịch nào</b> và endpoint công khai lặng lẽ trả ra
 * bình luận admin vừa ẩn (AGENTS.md §28.2).
 *
 * <p>Test ở mức POJO, không dựng Spring: controller chỉ còn điều hướng nên gọi thẳng được.
 */
class CommentControllerVisibilityTest {

    private static final String MOVIE_SLUG = "phim-hay";
    private static final String VIEWER_ID = "usr_viewer";

    private ListCommentsQuery listCommentsQuery;
    private ListCommentsAdminQuery listCommentsAdminQuery;
    private PublicCommentController publicController;
    private AdminCommentController adminController;
    private AppUserDetails principal;

    @BeforeEach
    void setUp() {
        listCommentsQuery = mock(ListCommentsQuery.class);
        listCommentsAdminQuery = mock(ListCommentsAdminQuery.class);

        publicController = new PublicCommentController(listCommentsQuery);
        adminController = new AdminCommentController(
                listCommentsAdminQuery, mock(SetCommentApprovalUseCase.class));

        principal = new AppUserDetails(VIEWER_ID, "nam@gmail.com", "x", true, true, List.of(), 0);

        when(listCommentsQuery.execute(any(), any(), any(PageQuery.class), any()))
                .thenReturn(new PageResult<>(List.of(), 0, 1, 15, 0));
        when(listCommentsAdminQuery.execute(any(), any(), any(), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 1, 15, 0));
    }

    private PageQueryParamDto params(String search) {
        return new PageQueryParamDto(null, null, search, null, null);
    }

    @Test
    void endpointCongKhai_goi_KHONG_token_van_chay_voi_viewerId_null() {
        // Đây là endpoint permitAll nên principal có thể null. Gọi userIdOf trực tiếp trên null
        // sẽ ném IllegalStateException và cả trang bình luận chết với khách vãng lai.
        publicController.list(MOVIE_SLUG, null, params(null), null);

        verify(listCommentsQuery).execute(eq(MOVIE_SLUG), isNull(), any(PageQuery.class), isNull());
    }

    @Test
    void endpointCongKhai_co_token_PHAI_chuyen_tiep_viewerId() {
        publicController.list(MOVIE_SLUG, null, params(null), principal);

        verify(listCommentsQuery).execute(
                eq(MOVIE_SLUG), isNull(), any(PageQuery.class), eq(VIEWER_ID));
    }

    @Test
    void endpointCongKhai_chuyen_tiep_episodeId_khi_co() {
        publicController.list(MOVIE_SLUG, "ep_5", params(null), principal);

        verify(listCommentsQuery).execute(
                eq(MOVIE_SLUG), eq("ep_5"), any(PageQuery.class), eq(VIEWER_ID));
    }

    @Test
    void endpointAdmin_khong_loc_theo_trangThaiDuyet_khi_khong_truyen() {
        // isApproved null nghĩa là admin thấy cả bình luận đã duyệt lẫn đang bị ẩn — đúng mục
        // đích của màn kiểm duyệt.
        adminController.list(null, null, params(null));

        verify(listCommentsAdminQuery).execute(isNull(), isNull(), isNull(), any(PageQuery.class));
    }

    @Test
    void endpointAdmin_chuyen_tiep_du_ba_bo_loc() {
        adminController.list("mov_1", Boolean.FALSE, params("tu khoa"));

        verify(listCommentsAdminQuery).execute(
                eq("mov_1"), eq(Boolean.FALSE), eq("tu khoa"), any(PageQuery.class));
    }
}
