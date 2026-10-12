package com.example.backend_pj4.application.query.favorite;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.dto.favorite.FavoriteMovieResult;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.ListFavorite;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.FavoriteRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.filter.FavoriteFilter;

/**
 * Khoá việc đã bỏ N+1 ở danh sách phim yêu thích.
 *
 * <p>Trước đây mỗi dòng favorite gọi một {@code movieRepository.findById} riêng, nên 300 phim
 * yêu thích là 301 truy vấn mỗi lần mở trang.
 */
class ListFavoriteMoviesQueryHandlerTest {

    private static final String USER_ID = "usr_1";
    private static final PageQuery QUERY = new PageQuery(0, 500, "createdAt", SortOrder.desc);
    private static final FavoriteFilter FILTER = new FavoriteFilter(USER_ID, null);

    private FavoriteRepository favoriteRepository;
    private MovieRepository movieRepository;
    private ListFavoriteMoviesQueryHandler handler;

    @BeforeEach
    void setUp() {
        favoriteRepository = mock(FavoriteRepository.class);
        movieRepository = mock(MovieRepository.class);
        StorageUrlResolver storageUrlResolver = mock(StorageUrlResolver.class);
        lenient().when(storageUrlResolver.resolvePublicImage(anyString())).thenReturn("http://x/p.jpg");

        handler = new ListFavoriteMoviesQueryHandler(favoriteRepository, movieRepository, storageUrlResolver);
    }

    private Movie movie(String id) {
        return Movie.builder().id(id).slug("phim-" + id).title("Phim " + id).posterUrl("p.jpg").build();
    }

    private PageResult<ListFavorite> page(List<ListFavorite> items) {
        return new PageResult<>(items, items.size(), 1, QUERY.size(), 1);
    }

    private void givenFavorites(int count) {
        List<ListFavorite> favorites = new ArrayList<>();
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            favorites.add(ListFavorite.builder().userId(USER_ID).movieId("m" + i).build());
            movies.add(movie("m" + i));
        }
        when(favoriteRepository.find(any(FavoriteFilter.class), any(PageQuery.class))).thenReturn(page(favorites));
        when(movieRepository.findByIds(any())).thenReturn(movies);
    }

    @Test
    void nhieuPhimYeuThich_chiChayDUNG_MOT_truyVanPhim() {
        givenFavorites(300);

        List<FavoriteMovieResult> result = handler.execute(FILTER, QUERY).items();

        assertThat(result).hasSize(300);
        verify(movieRepository, times(1)).findByIds(any());
        verify(movieRepository, never()).findById(anyString());
    }

    @Test
    void giuDungThuTuCuaBangFavorite() {
        givenFavorites(3);

        assertThat(handler.execute(FILTER, QUERY).items())
                .extracting(FavoriteMovieResult::movieId).containsExactly("m0", "m1", "m2");
    }

    @Test
    void phimDaXoa_biBoQua_khongLamVoDanhSach() {
        when(favoriteRepository.find(any(FavoriteFilter.class), any(PageQuery.class)))
                .thenReturn(page(List.of(
                        ListFavorite.builder().userId(USER_ID).movieId("con-ton-tai").build(),
                        ListFavorite.builder().userId(USER_ID).movieId("da-xoa").build())));
        when(movieRepository.findByIds(any())).thenReturn(List.of(movie("con-ton-tai")));

        assertThat(handler.execute(FILTER, QUERY).items())
                .extracting(FavoriteMovieResult::movieId).containsExactly("con-ton-tai");
    }

    @Test
    void khongCoPhimYeuThich_khongChayTruyVanPhimNao() {
        when(favoriteRepository.find(any(FavoriteFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 1, QUERY.size(), 0));

        assertThat(handler.execute(FILTER, QUERY).items()).isEmpty();

        verify(movieRepository, never()).findByIds(any(Collection.class));
    }

    @Test
    void search_duocDayXuongTANGDUOILIEU_khongLocBangJava() {
        givenFavorites(3);

        handler.execute(new FavoriteFilter(USER_ID, "co dau"), QUERY);

        // Lọc bằng Java thì totalItems sai và mỗi trang trả số lượng khác nhau.
        verify(favoriteRepository).find(eq(new FavoriteFilter(USER_ID, "co dau")), any(PageQuery.class));
    }

    @Test
    void addedAt_laNgayTHEM_VAO_YEU_THICH_khongPhaiNgayTaoPhim() {
        LocalDateTime ngayThemVaoYeuThich = LocalDateTime.of(2026, 10, 1, 8, 0);
        LocalDateTime ngayTaoPhim = LocalDateTime.of(2020, 1, 1, 0, 0);

        when(favoriteRepository.find(any(FavoriteFilter.class), any(PageQuery.class)))
                .thenReturn(page(List.of(ListFavorite.builder()
                        .userId(USER_ID).movieId("m1").createdAt(ngayThemVaoYeuThich).build())));
        when(movieRepository.findByIds(any())).thenReturn(List.of(Movie.builder()
                .id("m1").slug("phim-m1").title("Phim m1").posterUrl("p.jpg")
                .createdAt(ngayTaoPhim).build()));

        assertThat(handler.execute(FILTER, QUERY).items())
                .extracting(FavoriteMovieResult::addedAt)
                .containsExactly(ngayThemVaoYeuThich);
    }
}
