package com.example.backend_pj4.presentation.comment;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.comment.MyCommentResult;
import com.example.backend_pj4.application.port.in.comment.ListMyCommentsQuery;
import com.example.backend_pj4.common.annotation.AuthRequired;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Comments - Me")
@AuthRequired
@RestController
@RequestMapping("/api/v1/me/comments")
public class MyCommentController {

    private final ListMyCommentsQuery listMyCommentsQuery;

    public MyCommentController(ListMyCommentsQuery listMyCommentsQuery) {
        this.listMyCommentsQuery = listMyCommentsQuery;
    }

    @Operation(summary = "Xem lịch sử bình luận của chính mình (phân trang). Quyền truy cập: Người dùng đã đăng nhập.")
    @GetMapping
    public PageResult<MyCommentResult> listMyComments(
            @ModelAttribute PageQueryParamDto params,
            @AuthResultUser UserDetails userDetails
    ) {
        return listMyCommentsQuery.execute(
                AppUserDetails.userIdOf(userDetails),
                PageableFactory.create(params, ListMyCommentsQuery.SORTABLE, ListMyCommentsQuery.DEFAULT_SORT));
    }
}
