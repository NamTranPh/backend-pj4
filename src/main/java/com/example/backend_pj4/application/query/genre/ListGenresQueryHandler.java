package com.example.backend_pj4.application.query.genre;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.application.mapper.GenreResultMapper;
import com.example.backend_pj4.application.port.in.genre.ListGenresQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.GenreRepository;
import com.example.backend_pj4.domain.repository.filter.GenreFilter;

/**
 * Trước đây phải rẽ 4 nhánh if vì repository có 4 overload (có/không search, có/không status).
 * Gộp về một {@link GenreFilter} nên hết rẽ nhánh.
 */
@Service
public class ListGenresQueryHandler implements ListGenresQuery {

    private final GenreRepository genreRepository;

    public ListGenresQueryHandler(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<GenreResult> execute(GenreFilter filter, PageQuery query) {
        return genreRepository.find(filter, query).map(GenreResultMapper::toResult);
    }
}
