package com.example.backend_pj4.presentation.notification.request;

import org.springdoc.core.annotations.ParameterObject;

import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * DTO nhận tham số lấy danh sách thông báo của người dùng.
 */
@ParameterObject
public class RequestNotificationDto extends PageQueryParamDto {

    @Parameter(description = "Lọc theo trạng thái đã đọc (true: đã đọc, false: chưa đọc, null: tất cả)")
    private Boolean isRead;

    public RequestNotificationDto() {
    }

    public RequestNotificationDto(Boolean isRead, Integer page, Integer limit, String search, String sortBy, String sortDir) {
        super(page, limit, search, sortBy, sortDir);
        this.isRead = isRead;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
