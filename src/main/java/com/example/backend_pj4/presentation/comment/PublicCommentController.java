package com.example.backend_pj4.presentation.comment;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.comment.CommentResult;
import com.example.backend_pj4.application.port.in.comment.ListCommentsQuery;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Comments - Public")
@RestController
@RequestMapping("/api/v1/movies/{movieSlug}/comments")
public class PublicCommentController {

    private final ListCommentsQuery listCommentsQuery;

    public PublicCommentController(ListCommentsQuery listCommentsQuery) {
        this.listCommentsQuery = listCommentsQuery;
    }

    @Operation(summary = "Cây bình luận của phim hoặc của một tập. "
            + "Phân trang theo bình luận gốc, trả lời lồng trong trường replies. "
            + "Quyền truy cập: Public (Công khai).")
    @GetMapping
    public PageResult<CommentResult> list(
            @PathVariable String movieSlug,
            @RequestParam(required = false) String episodeId,
            @ModelAttribute PageQueryParamDto params,
            @AuthResultUser UserDetails userDetails
    ) {
        return listCommentsQuery.execute(
                movieSlug,
                episodeId,
                PageableFactory.create(params,
                        ListCommentsQuery.SORTABLE, ListCommentsQuery.DEFAULT_SORT),
                resolveViewerId(userDetails));
    }

    /**
     * Endpoint này cho cả khách vãng lai, nên principal có thể null — lúc đó chỉ không biết
     * người xem đã thích bình luận nào, còn cây bình luận vẫn trả bình thường.
     */
    private String resolveViewerId(UserDetails userDetails) {
        return userDetails == null ? null : AppUserDetails.userIdOf(userDetails);
    }
}
