package com.example.backend_pj4.presentation.genre.request;

import org.springdoc.core.annotations.ParameterObject;

import com.example.backend_pj4.common.constants.enums.GenreStatus;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

import io.swagger.v3.oas.annotations.Parameter;

@ParameterObject
public class RequestAdminGenreDto extends PageQueryParamDto {

    @Parameter(description = "Trạng thái thể loại: ACTIVE, INACTIVE")
    private GenreStatus status;

    public RequestAdminGenreDto() {
    }

    public RequestAdminGenreDto(GenreStatus status, Integer page, Integer limit, String search, String sortBy, String sortDir) {
        super(page, limit, search, sortBy, sortDir);
        this.status = status;
    }

    public GenreStatus getStatus() {
        return status;
    }

    public void setStatus(GenreStatus status) {
        this.status = status;
    }
}
