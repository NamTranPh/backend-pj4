package com.example.backend_pj4.application.query.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.mapper.UserResultMapper;
import com.example.backend_pj4.application.port.in.user.ListUsersQuery;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.domain.repository.filter.UserFilter;

@Service
public class ListUsersQueryHandler implements ListUsersQuery {

    private final UserRepository userRepository;
    private final UserResultMapper userResultMapper;

    public ListUsersQueryHandler(UserRepository userRepository, UserResultMapper userResultMapper) {
        this.userRepository = userRepository;
        this.userResultMapper = userResultMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<UserProfileResult> execute(UserFilter filter, PageQuery query) {
        return userRepository.find(filter, query).map(userResultMapper::toResult);
    }
}
