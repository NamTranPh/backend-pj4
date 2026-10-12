package com.example.backend_pj4.application.query.history;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.dto.history.WatchHistoryResult;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.HistoryWatching;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.HistoryWatchingRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.filter.HistoryFilter;

/**
 * Khoá việc gộp hai use case cũ (lịch sử xem và xem tiếp) thành một handler.
 *
 * <p>Rủi ro của việc gộp: nếu {@code unfinishedOnly} không được đẩy xuống tầng dữ liệu mà lọc
 * bằng Java sau khi lấy, mỗi trang sẽ trả về số lượng khác nhau và {@code totalItems} sai.
 */
class ListWatchHistoryQueryHandlerTest {

    private static final String USER_ID = "usr_1";
    private static final PageQuery QUERY = new PageQuery(0, 15, "watchedAt", SortOrder.desc);

    private HistoryWatchingRepository historyRepository;
    private MovieRepository movieRepository;
    private EpisodeRepository episodeRepository;
    private ListWatchHistoryQueryHandler handler;

    @BeforeEach
    void setUp() {
        historyRepository = mock(HistoryWatchingRepository.class);
        movieRepository = mock(MovieRepository.class);
        episodeRepository = mock(EpisodeRepository.class);
        StorageUrlResolver storageUrlResolver = mock(StorageUrlResolver.class);
        lenient().when(storageUrlResolver.resolvePublicImage(anyString())).thenReturn("http://x/p.jpg");
        lenient().when(episodeRepository.findByIds(any())).thenReturn(List.of());

        handler = new ListWatchHistoryQueryHandler(historyRepository, movieRepository,
                episodeRepository, storageUrlResolver);
    }

    /** 15 dòng trên một trang, nhưng database đếm được tổng 20 bản ghi khớp điều kiện. */
    private void givenPage(int rowsOnPage, long totalItems) {
        List<HistoryWatching> rows = new ArrayList<>();
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < rowsOnPage; i++) {
            rows.add(HistoryWatching.builder().id("h" + i).userId(USER_ID).movieId("m" + i).build());
            movies.add(Movie.builder().id("m" + i).slug("phim-" + i).title("Phim " + i).posterUrl("p.jpg").build());
        }
        when(historyRepository.find(any(HistoryFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(rows, totalItems, 1, 15, (int) Math.ceil(totalItems / 15.0)));
        when(movieRepository.findByIds(any())).thenReturn(movies);
    }

    @Test
    void xemTiep_dayCo_unfinishedOnly_XUONG_TANG_DU_LIEU() {
        givenPage(15, 20);

        handler.execute(new HistoryFilter(USER_ID, null, true), QUERY);

        // Lọc isCompleted bằng Java sau khi lấy thì mỗi trang trả số lượng khác nhau
        verify(historyRepository).find(eq(new HistoryFilter(USER_ID, null, true)), any(PageQuery.class));
    }

    @Test
    void lichSuXem_khongBatUnfinishedOnly() {
        givenPage(15, 20);

        handler.execute(new HistoryFilter(USER_ID, null, false), QUERY);

        verify(historyRepository).find(eq(new HistoryFilter(USER_ID, null, false)), any(PageQuery.class));
    }

    @Test
    void trangDayDu_traDung15Dong_vaTotalItemsLaySoCuaDatabase() {
        givenPage(15, 20);

        PageResult<WatchHistoryResult> result = handler.execute(new HistoryFilter(USER_ID, null, true), QUERY);

        assertThat(result.items()).hasSize(15);
        assertThat(result.totalItems())
                .as("tổng phải là số database đếm được, không phải số dòng trên trang")
                .isEqualTo(20);
    }

    @Test
    void search_dayXuongTangDuLieu() {
        givenPage(2, 2);

        handler.execute(new HistoryFilter(USER_ID, "co dau", false), QUERY);

        verify(historyRepository).find(eq(new HistoryFilter(USER_ID, "co dau", false)), any(PageQuery.class));
    }

    @Test
    void nhieuDongLichSu_chiChayMotTruyVanPhim() {
        givenPage(15, 20);

        handler.execute(new HistoryFilter(USER_ID, null, false), QUERY);

        verify(movieRepository, times(1)).findByIds(any());
        verify(movieRepository, never()).findById(anyString());
    }

    @Test
    void phimDaXoa_biBoQua_nhungTotalItemsKhongDoi() {
        when(historyRepository.find(any(HistoryFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(
                        HistoryWatching.builder().id("h1").userId(USER_ID).movieId("con-ton-tai").build(),
                        HistoryWatching.builder().id("h2").userId(USER_ID).movieId("da-xoa").build()),
                        2, 1, 15, 1));
        when(movieRepository.findByIds(any())).thenReturn(List.of(
                Movie.builder().id("con-ton-tai").slug("s").title("T").posterUrl("p.jpg").build()));

        PageResult<WatchHistoryResult> result = handler.execute(new HistoryFilter(USER_ID, null, false), QUERY);

        assertThat(result.items()).extracting(WatchHistoryResult::movieId).containsExactly("con-ton-tai");
        assertThat(result.totalItems()).isEqualTo(2);
    }

    @Test
    void khongCoLichSu_khongChayTruyVanPhimNao() {
        when(historyRepository.find(any(HistoryFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 1, 15, 0));

        assertThat(handler.execute(new HistoryFilter(USER_ID, null, false), QUERY).items()).isEmpty();

        verify(movieRepository, never()).findByIds(any());
    }
}
