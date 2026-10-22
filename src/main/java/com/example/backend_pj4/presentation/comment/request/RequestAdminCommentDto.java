package com.example.backend_pj4.presentation.comment.request;

import org.springdoc.core.annotations.ParameterObject;

import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * DTO nhận tham số lọc danh sách bình luận dành cho Quản trị viên (Admin).
 */
@ParameterObject
public class RequestAdminCommentDto extends PageQueryParamDto {

    @Parameter(description = "Lọc bình luận theo ID phim")
    private String movieId;

    @Parameter(description = "Lọc theo trạng thái duyệt (true: đã duyệt, false: chờ duyệt / bị ẩn)")
    private Boolean isApproved;

    public RequestAdminCommentDto() {
    }

    public RequestAdminCommentDto(String movieId, Boolean isApproved, Integer page, Integer limit, String search, String sortBy, String sortDir) {
        super(page, limit, search, sortBy, sortDir);
        this.movieId = movieId;
        this.isApproved = isApproved;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }
}
