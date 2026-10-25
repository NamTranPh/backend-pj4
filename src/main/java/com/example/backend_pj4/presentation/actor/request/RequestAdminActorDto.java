package com.example.backend_pj4.presentation.actor.request;

import org.springdoc.core.annotations.ParameterObject;

import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

import io.swagger.v3.oas.annotations.Parameter;

@ParameterObject
public class RequestAdminActorDto extends PageQueryParamDto {

    @Parameter(description = "Quốc tịch diễn viên")
    private String nationality;

    public RequestAdminActorDto() {
    }

    public RequestAdminActorDto(String nationality, Integer page, Integer limit, String search, String sortBy, String sortDir) {
        super(page, limit, search, sortBy, sortDir);
        this.nationality = nationality;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
