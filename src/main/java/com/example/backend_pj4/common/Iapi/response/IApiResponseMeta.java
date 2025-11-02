package com.example.backend_pj4.common.Iapi.response;

public interface IApiResponseMeta {
    boolean isStatus();
    String getMessage();
    Object getExtra();
}
