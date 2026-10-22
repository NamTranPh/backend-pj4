package com.example.backend_pj4.presentation.common.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;

/**
 * Base DTO tham số phân trang dùng chung cho mọi API lấy danh sách (Query Parameter DTO).
 */
@ParameterObject
public class PageQueryParamDto {

    @Parameter(description = "Số trang (bắt đầu từ 1, mặc định 1)", example = "1")
    private Integer page;

    @Parameter(description = "Số lượng phần tử mỗi trang (mặc định 15, tối đa 50)", example = "15")
    private Integer limit;

    @Parameter(description = "Từ khóa tìm kiếm")
    private String search;

    @Parameter(description = "Tên trường sắp xếp (theo whitelist của từng API)", example = "createdAt")
    private String sortBy;

    @Parameter(description = "Chiều sắp xếp", schema = @Schema(allowableValues = {"asc", "desc"}), example = "desc")
    private String sortDir;

    public PageQueryParamDto() {
    }

    public PageQueryParamDto(Integer page, Integer limit, String search, String sortBy, String sortDir) {
        this.page = page;
        this.limit = limit;
        this.search = search;
        this.sortBy = sortBy;
        this.sortDir = sortDir;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public Integer page() {
        return page;
    }

    public Integer limit() {
        return limit;
    }

    public String search() {
        return search;
    }

    public String sortBy() {
        return sortBy;
    }

    public String sortDir() {
        return sortDir;
    }
}


