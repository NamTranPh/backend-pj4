package com.example.backend_pj4.application.port.in.membership;

import com.example.backend_pj4.application.dto.membership.MyMembershipResult;

public interface GetMyActiveMembershipQuery {
    MyMembershipResult execute(String userId);
}
