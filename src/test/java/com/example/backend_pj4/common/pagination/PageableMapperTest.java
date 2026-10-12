package com.example.backend_pj4.common.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.infrastructure.database.adapters.PageableMapper;

/**
 * Khoá phép phiên dịch giữa phân trang thuần Java và Spring Data.
 *
 * <p>Đây là nơi duy nhất biết Spring Data ở ranh giới domain, nên sai ở đây là sai toàn hệ thống.
 */
class PageableMapperTest {

    @Test
    void pageQuery_thanhPageable_giuNguyenTrangVaKichThuoc() {
        Pageable pageable = PageableMapper.toPageable(new PageQuery(0, 15, "createdAt", SortOrder.desc));

        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(15);
    }

    @Test
    void sortOrderAsc_thanhDirectionASC() {
        Pageable pageable = PageableMapper.toPageable(new PageQuery(0, 15, "title", SortOrder.asc));

        assertThat(pageable.getSort().getOrderFor("title")).isNotNull();
        assertThat(pageable.getSort().getOrderFor("title").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void sortOrderDesc_thanhDirectionDESC() {
        Pageable pageable = PageableMapper.toPageable(new PageQuery(2, 7, "title", SortOrder.desc));

        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getSort().getOrderFor("title").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void trang0CuaSpringData_thanhTrang1ChoClient() {
        Page<String> page = new PageImpl<>(List.of("a"), PageRequest.of(0, 15), 1);

        assertThat(PageableMapper.toPageResult(page, s -> s).page())
                .as("PageQuery đếm từ 0, PageResult đếm từ 1, phép +1 chỉ nằm ở đây")
                .isEqualTo(1);
    }

    @Test
    void trang1CuaSpringData_thanhTrang2ChoClient() {
        Page<String> page = new PageImpl<>(List.of("a"), PageRequest.of(1, 15), 100);

        assertThat(PageableMapper.toPageResult(page, s -> s).page()).isEqualTo(2);
    }

    @Test
    void mangDayDuThongTinPhanTrang() {
        Page<String> page = new PageImpl<>(List.of("a", "b"), PageRequest.of(1, 10), 35);

        PageResult<String> result = PageableMapper.toPageResult(page, s -> s);

        assertThat(result.totalItems()).isEqualTo(35);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalPages()).isEqualTo(4);
    }

    @Test
    void apDungMapperChoTungPhanTu_giuNguyenThuTu() {
        Page<String> page = new PageImpl<>(List.of("a", "b", "c"), PageRequest.of(0, 15), 3);

        assertThat(PageableMapper.toPageResult(page, String::toUpperCase).items())
                .containsExactly("A", "B", "C");
    }

    @Test
    void bienTheNhanSanDanhSach_totalItemsVanLaySoCuaDatabase() {
        Page<String> page = new PageImpl<>(List.of("a", "b", "c"), PageRequest.of(0, 15), 300);

        // lọc bớt còn 1 phần tử, nhưng tổng vẫn là 300
        PageResult<String> result = PageableMapper.toPageResult(page, List.of("a"));

        assertThat(result.items()).hasSize(1);
        assertThat(result.totalItems())
                .as("tổng số bản ghi là thứ database biết, không phải số phần tử đếm trên một trang")
                .isEqualTo(300);
    }

    @Test
    void pageResultMap_doiKieuPhanTu_giuNguyenPhanTrang() {
        PageResult<String> result = new PageResult<>(List.of("a", "b"), 42, 3, 10, 5);

        PageResult<Integer> mapped = result.map(String::length);

        assertThat(mapped.items()).containsExactly(1, 1);
        assertThat(mapped.totalItems()).isEqualTo(42);
        assertThat(mapped.page()).isEqualTo(3);
        assertThat(mapped.size()).isEqualTo(10);
        assertThat(mapped.totalPages()).isEqualTo(5);
    }
}
