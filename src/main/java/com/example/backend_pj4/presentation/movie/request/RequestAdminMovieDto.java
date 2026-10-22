package com.example.backend_pj4.presentation.movie.request;

import org.springdoc.core.annotations.ParameterObject;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.common.constants.enums.VideoStatus;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * DTO nhận tham số lọc danh sách phim dành cho Quản trị viên (Admin).
 */
@ParameterObject
public class RequestAdminMovieDto extends RequestPublicMovieDto {

    @Parameter(description = "Trạng thái phim: DRAFT, PROCESSING, READY, FAILED, DELETED")
    private VideoStatus status;

    public RequestAdminMovieDto() {
    }

    public RequestAdminMovieDto(MovieType type, VideoStatus status, String genreId, Integer year, String country,
                               Integer page, Integer limit, String search, String sortBy, String sortDir) {
        super(type, genreId, year, country, page, limit, search, sortBy, sortDir);
        this.status = status;
    }

    public VideoStatus getStatus() {
        return status;
    }

    public void setStatus(VideoStatus status) {
        this.status = status;
    }
}
