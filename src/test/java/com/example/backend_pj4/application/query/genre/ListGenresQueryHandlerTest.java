package com.example.backend_pj4.application.query.genre;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.common.constants.enums.GenreStatus;
import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.domain.repository.filter.GenreFilter;

/**
 * Khoá 4 tổ hợp lọc sau khi gộp 4 overload của repository thành một {@link GenreFilter}.
 *
 * <p>Trước đây handler rẽ 4 nhánh if gọi 4 method khác nhau. Gộp lại mà nhầm một tổ hợp thì
 * admin sẽ không thấy thể loại đang tắt, hoặc người dùng lại thấy thể loại đã tắt.
 */
class ListGenresQueryHandlerTest {

    private static final PageQuery QUERY = new PageQuery(0, 15, "createdAt", SortOrder.desc);

    private GenreRepository genreRepository;
    private ListGenresQueryHandler handler;

    @BeforeEach
    void setUp() {
        genreRepository = mock(GenreRepository.class);
        handler = new ListGenresQueryHandler(genreRepository);
        when(genreRepository.find(any(GenreFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(
                        Genre.builder().id("g1").name("action").slug("action").status(GenreStatus.ACTIVE).build()),
                        1, 1, 15, 1));
    }

    // --- 4 tổ hợp: (có/không q) x (có/không status) ---

    @Test
    void toHop1_khongQ_khongStatus_thiKhongApDieuKienNao() {
        handler.execute(new GenreFilter(null, null), QUERY);

        verify(genreRepository).find(eq(new GenreFilter(null, null)), any(PageQuery.class));
    }

    @Test
    void toHop2_coQ_khongStatus_thiChiLocTheoTen() {
        handler.execute(new GenreFilter("act", null), QUERY);

        verify(genreRepository).find(eq(new GenreFilter("act", null)), any(PageQuery.class));
    }

    @Test
    void toHop3_khongQ_coStatus_thiChiLocTheoTrangThai() {
        handler.execute(new GenreFilter(null, GenreStatus.ACTIVE), QUERY);

        verify(genreRepository).find(eq(new GenreFilter(null, GenreStatus.ACTIVE)), any(PageQuery.class));
    }

    @Test
    void toHop4_coCaQVaStatus_thiApCaHai() {
        handler.execute(new GenreFilter("act", GenreStatus.ACTIVE), QUERY);

        verify(genreRepository).find(eq(new GenreFilter("act", GenreStatus.ACTIVE)), any(PageQuery.class));
    }

    // --- Ý nghĩa nghiệp vụ của status null vs ACTIVE ---

    @Test
    void publicController_truyenACTIVE_adminController_truyenNull() {
        // Đây là khác biệt duy nhất giữa /genres và /admin/genres sau khi gộp:
        // public chỉ thấy thể loại đang bật, admin thấy cả thể loại đã tắt.
        GenreFilter cuaPublic = new GenreFilter(null, GenreStatus.ACTIVE);
        GenreFilter cuaAdmin = new GenreFilter(null, null);

        assertThat(cuaPublic.status()).isEqualTo(GenreStatus.ACTIVE);
        assertThat(cuaAdmin.status()).as("null = không lọc, admin thấy hết").isNull();
    }

    @Test
    void traVeDungKetQuaDaMap() {
        PageResult<GenreResult> result = handler.execute(new GenreFilter(null, null), QUERY);

        assertThat(result.items()).extracting(GenreResult::name).containsExactly("action");
        assertThat(result.totalItems()).isEqualTo(1);
    }
}
