package com.example.backend_pj4.presentation.history;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.port.in.history.ListWatchHistoryQuery;
import com.example.backend_pj4.application.port.in.history.UpdateWatchProgressUseCase;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.HistoryFilter;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

/**
 * Hai endpoint lịch sử xem dùng chung một handler, khác nhau <b>đúng một cờ</b>
 * {@code unfinishedOnly}. Cờ đó do controller đặt, nên không test ở đây thì đổi nhầm
 * {@code true} thành {@code false} sẽ không có gì báo: "xem tiếp" lặng lẽ trả cả phim đã xem xong.
 *
 * <p>Test ở mức POJO, không dựng Spring: controller sau v4.9 chỉ còn điều hướng nên gọi thẳng được.
 */
class WatchHistoryControllerTest {

    private static final String USER_ID = "usr_1";

    private ListWatchHistoryQuery listWatchHistoryQuery;
    private WatchHistoryController controller;
    private AppUserDetails principal;

    @BeforeEach
    void setUp() {
        listWatchHistoryQuery = mock(ListWatchHistoryQuery.class);
        controller = new WatchHistoryController(mock(UpdateWatchProgressUseCase.class), listWatchHistoryQuery);
        principal = new AppUserDetails(USER_ID, "nam@gmail.com", "x", true, true, List.of(), 0);

        when(listWatchHistoryQuery.execute(any(HistoryFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 1, 15, 0));
    }

    private PageQueryParamDto params(String search) {
        return new PageQueryParamDto(null, null, search, null, null);
    }

    @Test
    void continueWatching_PHAI_bat_unfinishedOnly() {
        controller.continueWatching(principal, params(null));

        verify(listWatchHistoryQuery).execute(
                eq(new HistoryFilter(USER_ID, null, true)), any(PageQuery.class));
    }

    @Test
    void lichSuXem_PHAI_tat_unfinishedOnly() {
        controller.history(principal, params(null));

        verify(listWatchHistoryQuery).execute(
                eq(new HistoryFilter(USER_ID, null, false)), any(PageQuery.class));
    }

    @Test
    void lichSuXem_chuyenTiepTuKhoaTimKiem() {
        controller.history(principal, params("co dau"));

        verify(listWatchHistoryQuery).execute(
                eq(new HistoryFilter(USER_ID, "co dau", false)), any(PageQuery.class));
    }

    @Test
    void continueWatching_khongNhanTuKhoaTimKiem() {
        // Danh sách xem dở vốn ngắn, không mở search; truyền vào cũng bị bỏ qua.
        controller.continueWatching(principal, params("co dau"));

        verify(listWatchHistoryQuery).execute(
                eq(new HistoryFilter(USER_ID, null, true)), any(PageQuery.class));
    }
}
