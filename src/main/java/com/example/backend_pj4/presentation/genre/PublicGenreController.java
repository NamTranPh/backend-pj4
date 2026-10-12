package com.example.backend_pj4.presentation.genre;

import com.example.backend_pj4.common.pagination.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.application.port.in.genre.ListGenresQuery;
import com.example.backend_pj4.common.constants.enums.GenreStatus;
import com.example.backend_pj4.domain.repository.filter.GenreFilter;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Genres - Public")
@RestController
@RequestMapping("/api/v1/genres")
public class PublicGenreController {

    private final ListGenresQuery listGenresQuery;

    public PublicGenreController(ListGenresQuery listGenresQuery) {
        this.listGenresQuery = listGenresQuery;
    }

    @Operation(summary = "Lấy danh sách thể loại phim (Công khai). Quyền truy cập: Public (Công khai).")
    @GetMapping
    public PageResult<GenreResult> list(@ModelAttribute PageQueryParamDto params) {
        return listGenresQuery.execute(
                new GenreFilter(params.search(), GenreStatus.ACTIVE),
                PageableFactory.create(params, ListGenresQuery.SORTABLE, ListGenresQuery.DEFAULT_SORT));
    }
}
