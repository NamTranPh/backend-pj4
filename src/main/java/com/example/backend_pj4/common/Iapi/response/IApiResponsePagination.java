package com.example.backend_pj4.common.Iapi.response;

public interface IApiResponsePagination {
    int getPage();

    int getLimit();

    long getTotalItems();

    int getTotalPages();
}
