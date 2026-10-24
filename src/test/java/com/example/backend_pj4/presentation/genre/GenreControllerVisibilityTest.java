package com.example.backend_pj4.presentation.genre;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.port.in.genre.CreateGenreUseCase;
import com.example.backend_pj4.application.port.in.genre.DeleteGenreUseCase;
import com.example.backend_pj4.application.port.in.genre.GetGenreByIdUseCase;
import com.example.backend_pj4.application.port.in.genre.ListGenresQuery;
import com.example.backend_pj4.application.port.in.genre.UpdateGenreUseCase;
import com.example.backend_pj4.common.constants.enums.GenreStatus;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.filter.GenreFilter;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

import com.example.backend_pj4.presentation.genre.request.RequestAdminGenreDto;

/**
 * Sau khi gộp 4 overload của repository thành một {@link GenreFilter}, khác biệt duy nhất giữa
 * API công khai và API quản trị là <b>giá trị {@code status} mà controller truyền vào</b>.
 *
 * <p>Đổi nhầm thì không có lỗi biên dịch nào: trang công khai sẽ lặng lẽ hiện cả thể loại
 * admin đã tắt, hoặc trang quản trị mất hẳn các thể loại đã tắt.
 */
class GenreControllerVisibilityTest {

    private ListGenresQuery listGenresQuery;
    private PublicGenreController publicController;
    private AdminGenreController adminController;

    @BeforeEach
    void setUp() {
        listGenresQuery = mock(ListGenresQuery.class);
        when(listGenresQuery.execute(any(GenreFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(), 0, 1, 15, 0));

        publicController = new PublicGenreController(listGenresQuery);
        adminController = new AdminGenreController(
                mock(CreateGenreUseCase.class),
                mock(UpdateGenreUseCase.class),
                mock(GetGenreByIdUseCase.class),
                listGenresQuery,
                mock(DeleteGenreUseCase.class));
    }

    private PageQueryParamDto params(String search) {
        return new PageQueryParamDto(null, null, search, null, null);
    }

    private RequestAdminGenreDto adminParams(String search) {
        return new RequestAdminGenreDto(null, null, null, search, null, null);
    }

    @Test
    void apiCongKhai_CHI_lay_the_loai_dang_bat() {
        publicController.list(params(null));

        verify(listGenresQuery).execute(
                eq(new GenreFilter(null, GenreStatus.ACTIVE)), any(PageQuery.class));
    }

    @Test
    void apiQuanTri_lay_ca_the_loai_da_tat() {
        adminController.list(adminParams(null));

        // status = null nghĩa là không áp điều kiện, không phải "lấy thể loại có status null"
        verify(listGenresQuery).execute(
                eq(new GenreFilter(null, null)), any(PageQuery.class));
    }

    @Test
    void apiCongKhai_timKiem_van_giu_rang_buoc_ACTIVE() {
        publicController.list(params("act"));

        verify(listGenresQuery).execute(
                eq(new GenreFilter("act", GenreStatus.ACTIVE)), any(PageQuery.class));
    }

    @Test
    void apiQuanTri_timKiem_khong_bi_rang_buoc_trang_thai() {
        adminController.list(adminParams("act"));

        verify(listGenresQuery).execute(
                eq(new GenreFilter("act", null)), any(PageQuery.class));
    }

    @Test
    void apiQuanTri_locTheoTrangThai_truyenDungTrangThaiVaoFilter() {
        RequestAdminGenreDto dto = new RequestAdminGenreDto(GenreStatus.INACTIVE, null, null, null, null, null);
        adminController.list(dto);

        verify(listGenresQuery).execute(
                eq(new GenreFilter(null, GenreStatus.INACTIVE)), any(PageQuery.class));
    }
}
