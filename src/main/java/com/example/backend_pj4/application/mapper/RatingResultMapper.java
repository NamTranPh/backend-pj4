package com.example.backend_pj4.application.mapper;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.dto.rating.RatingResult;
import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.model.User;

/**
 * Là {@code @Component} chứ không phải utility tĩnh vì cần {@link CommentResultMapper} để dựng
 * khối tác giả — trong đó có bước đổi objectKey ảnh đại diện thành URL (AGENTS.md §31.5).
 */
@Component
public class RatingResultMapper {

    private final CommentResultMapper commentResultMapper;

    public RatingResultMapper(CommentResultMapper commentResultMapper) {
        this.commentResultMapper = commentResultMapper;
    }

    public RatingResult toResult(Rating rating, User author) {
        return new RatingResult(
                rating.getId(),
                rating.getMovieId(),
                rating.getScore() == null ? 0 : rating.getScore(),
                rating.getReview(),
                commentResultMapper.toAuthor(rating.getUserId(), author),
                rating.getCreatedAt(),
                rating.getUpdatedAt());
    }
}
