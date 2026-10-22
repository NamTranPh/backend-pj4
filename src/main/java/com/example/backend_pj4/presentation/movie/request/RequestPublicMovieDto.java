package com.example.backend_pj4.presentation.movie.request;

import org.springdoc.core.annotations.ParameterObject;

import com.example.backend_pj4.common.constants.enums.MovieType;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * DTO nhận tham số tìm kiếm và lọc danh sách phim công khai.
 */
@ParameterObject
public class RequestPublicMovieDto extends PageQueryParamDto {

    @Parameter(description = "Loại phim: SINGLE (Phim lẻ), SERIES (Phim bộ)")
    private MovieType type;

    @Parameter(description = "Lọc theo ID thể loại phim")
    private String genreId;

    @Parameter(description = "Lọc theo năm phát hành", example = "2026")
    private Integer year;

    @Parameter(description = "Lọc theo quốc gia sản xuất", example = "Vietnam")
    private String country;

    public RequestPublicMovieDto() {
    }

    public RequestPublicMovieDto(MovieType type, String genreId, Integer year, String country,
                                Integer page, Integer limit, String search, String sortBy, String sortDir) {
        super(page, limit, search, sortBy, sortDir);
        this.type = type;
        this.genreId = genreId;
        this.year = year;
        this.country = country;
    }

    public MovieType getType() {
        return type;
    }

    public void setType(MovieType type) {
        this.type = type;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
