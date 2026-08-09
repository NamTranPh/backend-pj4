package com.example.backend_pj4.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Comment {
    private String id;
    private String userId;
    private String movieId;
    private String episodeId;
    private String content;
    private String parentId;
    private Boolean isApproved;
    private Integer likeCount;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
