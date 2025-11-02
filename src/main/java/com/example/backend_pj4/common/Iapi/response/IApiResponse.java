package com.example.backend_pj4.common.Iapi.response;

public interface IApiResponse<T> {
    IApiResponseMeta getMeta();

    T getData();

    IApiResponsePagination getPagination();
}
