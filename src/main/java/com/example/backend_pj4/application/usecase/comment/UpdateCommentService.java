package com.example.backend_pj4.application.usecase.comment;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.comment.CommentResult;
import com.example.backend_pj4.application.mapper.CommentResultMapper;
import com.example.backend_pj4.application.port.in.comment.UpdateCommentUseCase;
import com.example.backend_pj4.application.port.out.CommentListCache;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class UpdateCommentService implements UpdateCommentUseCase {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentResultMapper commentResultMapper;
    private final CommentListCache commentListCache;

    public UpdateCommentService(CommentRepository commentRepository,
                                UserRepository userRepository,
                                CommentResultMapper commentResultMapper,
                                CommentListCache commentListCache) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.commentResultMapper = commentResultMapper;
        this.commentListCache = commentListCache;
    }

    @Override
    @Transactional
    public CommentResult execute(String commentId, String userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // Kiểm tra quyền sở hữu ở use case chứ không chỉ ở endpoint (AGENTS.md §17): endpoint
        // chỉ biết "đã đăng nhập", nó không biết bình luận này của ai.
        if (!Objects.equals(comment.getUserId(), userId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_OWNED);
        }

        // toBuilder giữ nguyên rootId, parentId, likeCount, isApproved — sửa nội dung không được
        // làm bình luận rơi khỏi cây hay tự bỏ trạng thái bị ẩn.
        Comment saved = commentRepository.save(comment.toBuilder().content(content).build());

        commentListCache.bumpVersion(comment.getMovieId());

        return commentResultMapper.toResult(saved, userRepository.findById(userId).orElse(null));
    }
}
