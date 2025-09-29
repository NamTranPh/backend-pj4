package com.example.backend_pj4.domain.entities;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Comment {
    private String commentId;
    private User user;
    private Movie movie;
    private Episode episode;
    private String content;
    private Comment parent;
    private List<Comment> replies;
    private Boolean isApproved;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}