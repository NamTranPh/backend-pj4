package com.example.backend_pj4.application.usecase.comment;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.comment.CreateCommentCommand;
import com.example.backend_pj4.application.dto.comment.CommentResult;
import com.example.backend_pj4.application.mapper.CommentResultMapper;
import com.example.backend_pj4.application.port.in.comment.CreateCommentUseCase;
import com.example.backend_pj4.application.port.out.CommentListCache;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.NotificationReferenceType;
import com.example.backend_pj4.common.constants.enums.NotificationType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.Notification;
import com.example.backend_pj4.domain.model.User;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.NotificationRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

@Service
public class CreateCommentService implements CreateCommentUseCase {

    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final CommentResultMapper commentResultMapper;
    private final CommentListCache commentListCache;

    public CreateCommentService(MovieRepository movieRepository,
                                EpisodeRepository episodeRepository,
                                CommentRepository commentRepository,
                                UserRepository userRepository,
                                NotificationRepository notificationRepository,
                                CommentResultMapper commentResultMapper,
                                CommentListCache commentListCache) {
        this.movieRepository = movieRepository;
        this.episodeRepository = episodeRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.commentResultMapper = commentResultMapper;
        this.commentListCache = commentListCache;
    }

    @Override
    @Transactional
    public CommentResult execute(CreateCommentCommand command) {
        Movie movie = movieRepository.findBySlug(command.movieSlug())
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        Comment parent = resolveParent(command, movie);
        String episodeId = resolveEpisodeId(command, movie, parent);

        Comment saved = commentRepository.save(Comment.builder()
                .userId(command.userId())
                .movieId(movie.getId())
                .episodeId(episodeId)
                .content(command.content())
                .parentId(parent == null ? null : parent.getId())
                .rootId(Comment.resolveRootId(parent))
                // Đặt rõ hai giá trị này thay vì để null: cột nullable và không có DEFAULT ở
                // database, mà truy vấn danh sách lại lọc isApproved = true — để null là bình
                // luận vừa đăng không hiện ra.
                .isApproved(Boolean.TRUE)
                .likeCount(0)
                .build());

        notifyParentAuthor(parent, saved);

        // Vô hiệu hoá cache sau khi ghi xong. Nếu transaction rollback sau bước này thì hậu quả
        // chỉ là cache bị xoá thừa, lần đọc kế tiếp nạp lại từ database — vô hại.
        commentListCache.bumpVersion(movie.getId());

        User author = userRepository.findById(command.userId()).orElse(null);
        return commentResultMapper.toResult(saved, author);
    }

    /**
     * Bình luận con <b>kế thừa</b> phạm vi của cha và không tin giá trị trong request.
     *
     * <p>Nếu tin body thì client gửi {@code parentId} của phim A kèm {@code movieSlug} của phim B
     * sẽ tạo ra một nhánh mồ côi: nó có cha ở phim A nhưng được gắn vào phim B, không bao giờ
     * hiện ra ở cả hai nơi.
     */
    private Comment resolveParent(CreateCommentCommand command, Movie movie) {
        if (command.parentId() == null || command.parentId().isBlank()) {
            return null;
        }

        // findById đã lọc sẵn bình luận bị xoá mềm, nên trả lời vào một bình luận đã xoá cũng
        // rơi vào nhánh này.
        Comment parent = commentRepository.findById(command.parentId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_PARENT_NOT_FOUND));

        if (!Objects.equals(parent.getMovieId(), movie.getId())) {
            throw new CustomException(ErrorCode.COMMENT_PARENT_MISMATCH);
        }

        return parent;
    }

    private String resolveEpisodeId(CreateCommentCommand command, Movie movie, Comment parent) {
        if (parent != null) {
            return parent.getEpisodeId();
        }

        if (command.episodeId() == null || command.episodeId().isBlank()) {
            return null;
        }

        Episode episode = episodeRepository.findById(command.episodeId())
                .orElseThrow(() -> new CustomException(ErrorCode.EPISODE_NOT_FOUND));

        // Tập của phim khác thì báo không tìm thấy chứ không báo "sai phim": người gọi không có
        // quyền biết tập đó tồn tại ở đâu.
        if (!Objects.equals(episode.getMovieId(), movie.getId())) {
            throw new CustomException(ErrorCode.EPISODE_NOT_FOUND);
        }

        return episode.getId();
    }

    /**
     * Báo cho tác giả bình luận cha biết có người trả lời.
     *
     * <p>Không tạo cổng {@code CreateNotificationUseCase} riêng cho một chỗ gọi duy nhất — một
     * interface với một cài đặt và một caller là lớp trung gian không đỡ được gì.
     */
    private void notifyParentAuthor(Comment parent, Comment reply) {
        if (parent == null) {
            return;
        }
        // Tự trả lời chính mình thì không tự báo cho mình.
        if (Objects.equals(parent.getUserId(), reply.getUserId())) {
            return;
        }

        notificationRepository.save(Notification.builder()
                .userId(parent.getUserId())
                .type(NotificationType.COMMENT_REPLY)
                .title("Có người trả lời bình luận của bạn")
                .content(reply.getContent())
                .referenceType(NotificationReferenceType.COMMENT)
                .referenceId(reply.getId())
                .isRead(false)
                .build());
    }
}
