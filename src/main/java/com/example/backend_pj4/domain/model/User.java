package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import com.example.backend_pj4.common.constants.enums.AccountStatus;
import com.example.backend_pj4.common.constants.enums.UserRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class User {
    private String id;
    private String email;
    private String password;
    private String name;
    private String profileUrl;
    private String phone;
    private String address;
    private UserRole role;
    private Boolean emailVerified;
    private AccountStatus accountStatus;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
