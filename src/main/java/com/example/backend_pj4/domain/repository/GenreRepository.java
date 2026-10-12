package com.example.backend_pj4.domain.repository;

import java.util.List;
import java.util.Optional;

import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Genre;
import com.example.backend_pj4.domain.repository.filter.GenreFilter;

public interface GenreRepository {
    Genre save(Genre genre);

    Optional<Genre> findById(String id);

    /**
     * Danh sách thể loại, có lọc, sắp xếp và phân trang.
     *
     * <p>Thay cho 4 overload trước đây: findAll, findByStatus, findByNameContainingAndStatus,
     * findByNameContaining. Mỗi trường null trong {@link GenreFilter} là không áp điều kiện đó.
     */
    PageResult<Genre> find(GenreFilter filter, PageQuery query);

    /** Nạp hàng loạt theo danh sách id, thay cho việc gọi findById trong vòng lặp (N+1). */
    List<Genre> findByIds(List<String> ids);

    Optional<Genre> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    /** Gỡ thể loại khỏi mọi phim trước khi xoá nó. */
    void removeGenreFromAllMovies(String genreId);

    void deleteById(String id);
}
