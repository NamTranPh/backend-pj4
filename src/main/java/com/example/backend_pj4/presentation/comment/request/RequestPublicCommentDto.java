package com.example.backend_pj4.presentation.comment.request;

import org.springdoc.core.annotations.ParameterObject;

import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * DTO nhận tham số lấy danh sách bình luận công khai cho một phim.
 */
@ParameterObject
public class RequestPublicCommentDto extends PageQueryParamDto {

    @Parameter(description = "Lọc bình luận theo ID tập phim (để trống nếu muốn lấy bình luận của cả phim)")
    private String episodeId;

    public RequestPublicCommentDto() {
    }

    public RequestPublicCommentDto(String episodeId, Integer page, Integer limit, String search, String sortBy, String sortDir) {
        super(page, limit, search, sortBy, sortDir);
        this.episodeId = episodeId;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }
}
