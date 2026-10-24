package com.example.backend_pj4.application.port.in.movie;

import java.util.List;
import com.example.backend_pj4.application.dto.movie.MovieResult;

/** Cổng ĐỌC danh sách phim tương tự / liên quan. */
public interface GetRelatedMoviesQuery {
    List<MovieResult> execute(String slug, int limit);
}
