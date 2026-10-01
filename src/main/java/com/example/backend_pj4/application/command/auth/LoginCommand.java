package com.example.backend_pj4.application.command.auth;

/** userAgent/ipAddress phục vụ màn hình quản lý phiên đăng nhập, có thể null. */
public record LoginCommand(String email, String password, String userAgent, String ipAddress) {}
