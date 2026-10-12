package com.example.backend_pj4.presentation.common.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Set;

import org.junit.jupiter.api.Test;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.pagination.PageQuery;

/**
 * Khoá toàn bộ luật phân trang. Đây là nơi duy nhất biết các luật này, nên mọi thay đổi hành vi
 * phân trang của hệ thống đều phải đi qua test ở đây.
 */
class PageableFactoryTest {

    private static final Set<String> SORTABLE = Set.of("createdAt", "title", "viewCount");
    private static final String DEFAULT_SORT = "createdAt";

    private PageQuery create(Integer page, Integer limit, String sortBy, String sortDir) {
        return PageableFactory.create(
                new PageQueryParamDto(page, limit, null, sortBy, sortDir), SORTABLE, DEFAULT_SORT);
    }

    private ErrorCode errorOf(Runnable action) {
        try {
            action.run();
            throw new AssertionError("mong đợi CustomException nhưng không có lỗi nào");
        } catch (CustomException e) {
            return e.getErrorCode();
        }
    }

    // --- Giá trị mặc định ---

    @Test
    void khongTruyenGi_thiTrang1_15Dong_sapXepMacDinhGiamDan() {
        PageQuery query = create(null, null, null, null);

        assertThat(query.page()).isZero();
        assertThat(query.size()).isEqualTo(15);
        assertThat(query.sortBy()).isEqualTo(DEFAULT_SORT);
        assertThat(query.direction()).isEqualTo(SortOrder.desc);
    }

    @Test
    void paramsNull_vanTraVeMacDinh_khongNemNullPointer() {
        PageQuery query = PageableFactory.create(null, SORTABLE, DEFAULT_SORT);

        assertThat(query.size()).isEqualTo(15);
    }

    // --- Đánh số trang: URL từ 1, Spring Data từ 0 ---

    @Test
    void trang1CuaUrl_thanhTrang0CuaSpringData() {
        assertThat(create(1, null, null, null).page()).isZero();
    }

    @Test
    void trang3CuaUrl_thanhTrang2CuaSpringData() {
        assertThat(create(3, null, null, null).page()).isEqualTo(2);
    }

    @Test
    void trangAmHoac0_vanVeTrangDau_khongNem() {
        assertThat(create(0, null, null, null).page()).isZero();
        assertThat(create(-5, null, null, null).page()).isZero();
    }

    // --- Trần kích thước trang ---

    @Test
    void limitVuotTran_biEpVe50() {
        assertThat(create(null, 1_000_000, null, null).size())
                .as("client không được kéo cả bảng về bằng một request")
                .isEqualTo(50);
    }

    @Test
    void limitHopLe_duocGiuNguyen() {
        assertThat(create(null, 7, null, null).size()).isEqualTo(7);
        assertThat(create(null, 50, null, null).size()).isEqualTo(50);
    }

    @Test
    void limitAmHoac0_quayVeMacDinh() {
        assertThat(create(null, 0, null, null).size()).isEqualTo(15);
        assertThat(create(null, -3, null, null).size()).isEqualTo(15);
    }

    // --- Whitelist sắp xếp: lớp chặn, phải khoá kỹ ---

    @Test
    void sortByNgoaiWhitelist_biTuChoi() {
        assertThat(errorOf(() -> create(null, null, "password", null)))
                .as("sortBy đi thẳng vào tên cột SQL, không lọc là lộ cấu trúc bảng")
                .isEqualTo(ErrorCode.BAD_REQUEST);
    }

    @Test
    void sortByTrongWhitelist_duocChapNhan() {
        assertThat(create(null, null, "title", null).sortBy()).isEqualTo("title");
    }

    @Test
    void sortByRongHoacTrangTrang_dungFieldMacDinh() {
        assertThat(create(null, null, "", null).sortBy()).isEqualTo(DEFAULT_SORT);
        assertThat(create(null, null, "   ", null).sortBy()).isEqualTo(DEFAULT_SORT);
    }

    @Test
    void sortByCoKhoangTrangThua_vanNhanDung() {
        assertThat(create(null, null, "  title  ", null).sortBy()).isEqualTo("title");
    }

    // --- Chiều sắp xếp ---

    @Test
    void sortDirAsc_thanhTangDan() {
        assertThat(create(null, null, "title", "asc").direction())
                .isEqualTo(SortOrder.asc);
    }

    @Test
    void sortDirKhongHopLeHoacThieu_macDinhGiamDan() {
        assertThat(create(null, null, "title", null).direction())
                .isEqualTo(SortOrder.desc);
        assertThat(create(null, null, "title", "lung-tung").direction())
                .isEqualTo(SortOrder.desc);
    }

    @Test
    void sortDirKhongPhanBietHoaThuong() {
        assertThat(create(null, null, "title", "ASC").direction())
                .isEqualTo(SortOrder.asc);
    }

    // --- Biến thể cho module không cho client chọn sắp xếp ---

    @Test
    void bienTheSortCoDinh_chiChapNhanDungFieldDo() {
        PageQueryParamDto params = new PageQueryParamDto(null, null, null, null, null);

        assertThatCode(() -> PageableFactory.create(params, "lastUsedAt")).doesNotThrowAnyException();

        PageQueryParamDto coGangDoiSort = new PageQueryParamDto(null, null, null, "password", null);
        assertThat(errorOf(() -> PageableFactory.create(coGangDoiSort, "lastUsedAt")))
                .isEqualTo(ErrorCode.BAD_REQUEST);
    }
}
