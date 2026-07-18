package com.example.backend_pj4.application.services.comment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.exceptions.ResourceNotFoundException;
import com.example.backend_pj4.common.base.BaseService;
import com.example.backend_pj4.domain.entities.Comment;
import com.example.backend_pj4.domain.entities.Episode;
import com.example.backend_pj4.domain.entities.Movie;
import com.example.backend_pj4.domain.entities.User;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService extends BaseService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;

    public Comment createComment(String userId, String movieId, String episodeId, String content, String parentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        Episode episode = null;
        if (episodeId != null && !episodeId.isEmpty()) {
            episode = episodeRepository.findById(episodeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
        }

        Comment parent = null;
        if (parentId != null && !parentId.isEmpty()) {
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
        }

        Comment comment = Comment.builder()
                .content(content)
                .isApproved(false)
                .likeCount(0)
                .build();

        comment.setUser(user);
        comment.setMovie(movie);
        comment.setEpisode(episode);
        comment.setParent(parent);

        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByMovie(String movieId) {
        return commentRepository.findByMovieIdAndParentIsNull(movieId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByEpisode(String episodeId) {
        return commentRepository.findByEpisodeIdAndParentIsNull(episodeId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getApprovedCommentsByMovie(String movieId) {
        return commentRepository.findApprovedCommentsByMovieId(movieId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getPendingComments() {
        return commentRepository.findPendingComments();
    }

    @Transactional(readOnly = true)
    public List<Comment> getTopCommentsByMovie(String movieId, int limit) {
        return commentRepository.findTopCommentsByMovieId(movieId, limit);
    }

    @Transactional(readOnly = true)
    public List<Comment> getReplies(String parentId) {
        return commentRepository.findByParentId(parentId);
    }

    public Comment approveComment(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        comment.setIsApproved(true);
        return commentRepository.save(comment);
    }

    public Comment updateComment(String commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void deleteComment(String commentId) {
        if (commentRepository.findById(commentId).isEmpty()) {
            throw new ResourceNotFoundException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }

    public Comment likeComment(String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        comment.setLikeCount(comment.getLikeCount() != null ? comment.getLikeCount() + 1 : 1);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public long countByMovie(String movieId) {
        return commentRepository.countByMovieId(movieId);
    }

    @Transactional(readOnly = true)
    public long countPending() {
        return commentRepository.countPendingComments();
    }
}
