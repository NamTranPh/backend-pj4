package com.example.backend_pj4.application.port.in.comment;

import com.example.backend_pj4.application.command.comment.CreateCommentCommand;
import com.example.backend_pj4.application.dto.comment.CommentResult;

public interface CreateCommentUseCase {
    CommentResult execute(CreateCommentCommand command);
}
