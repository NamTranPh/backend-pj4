package com.example.backend_pj4.application.usecase.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.backend_pj4.application.command.comment.CreateCommentCommand;
import com.example.backend_pj4.application.mapper.CommentResultMapper;
import com.example.backend_pj4.application.port.out.CommentListCache;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.NotificationType;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Comment;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.Notification;
import com.example.backend_pj4.domain.repository.CommentRepository;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.NotificationRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

/**
 * Khoá ba thứ mà hỏng là hỏng âm thầm, không có lỗi biên dịch nào báo:
 *
 * <ol>
 *   <li><b>{@code rootId} khi trả lời một bình luận cháu.</b> Lấy {@code parent.getId()} thay vì
 *       {@code parent.getRootId()} thì nhánh đó tách khỏi gốc và biến mất khỏi danh sách —
 *       truy vấn nạp con cháu đi theo {@code root_id}.</li>
 *   <li><b>Phạm vi kế thừa từ bình luận cha.</b> Tin {@code movieSlug} trong request thay vì
 *       dùng phim của bình luận cha sẽ tạo ra nhánh mồ côi nằm sai phim.</li>
 *   <li><b>Không tự báo cho chính mình.</b> Tự trả lời bình luận của mình mà vẫn sinh thông báo
 *       thì chuông kêu vô nghĩa.</li>
 * </ol>
 */
class CreateCommentServiceTest {

    private static final String USER_A = "usr_a";
    private static final String USER_B = "usr_b";
    private static final String MOVIE_ID = "mov_1";
    private static final String MOVIE_SLUG = "phim-hay";

    private MovieRepository movieRepository;
    private EpisodeRepository episodeRepository;
    private CommentRepository commentRepository;
    private NotificationRepository notificationRepository;
    private CommentListCache commentListCache;
    private CreateCommentService service;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        episodeRepository = mock(EpisodeRepository.class);
        commentRepository = mock(CommentRepository.class);
        notificationRepository = mock(NotificationRepository.class);
        commentListCache = mock(CommentListCache.class);

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        service = new CreateCommentService(
                movieRepository, episodeRepository, commentRepository, userRepository,
                notificationRepository, new CommentResultMapper(mock(StorageUrlResolver.class)),
                commentListCache);

        when(movieRepository.findBySlug(MOVIE_SLUG))
                .thenReturn(Optional.of(Movie.builder().id(MOVIE_ID).slug(MOVIE_SLUG).build()));
        // save trả lại đúng thứ nhận vào, để assert được giá trị service đã dựng.
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(inv -> inv.getArgument(0, Comment.class).toBuilder().id("cmt_new").build());
    }

    private Comment savedComment() {
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void binhLuanGoc_coRootIdNull_vaDuocDuyetSan() {
        service.execute(new CreateCommentCommand(USER_A, MOVIE_SLUG, null, null, "Phim hay"));

        Comment saved = savedComment();
        assertThat(saved.getParentId()).isNull();
        assertThat(saved.getRootId()).isNull();
        // Cột nullable, không có DEFAULT ở database, mà truy vấn danh sách lại lọc isApproved
        // = true — để null là bình luận vừa đăng không hiện ra.
        assertThat(saved.getIsApproved()).isTrue();
        assertThat(saved.getLikeCount()).isZero();
    }

    @Test
    void traLoiBinhLuanGoc_rootId_PHAI_laIdCuaGoc() {
        Comment root = Comment.builder()
                .id("cmt_root").userId(USER_B).movieId(MOVIE_ID).rootId(null).build();
        when(commentRepository.findById("cmt_root")).thenReturn(Optional.of(root));

        service.execute(new CreateCommentCommand(USER_A, MOVIE_SLUG, null, "cmt_root", "Dong y"));

        assertThat(savedComment().getRootId()).isEqualTo("cmt_root");
    }

    @Test
    void traLoiBinhLuanChau_rootId_PHAI_laGocChuKhongPhaiCha() {
        // Đây là chỗ dễ sai nhất: cha là cmt_child (cấp 2), nhưng rootId phải trỏ về cmt_root.
        Comment grandChildParent = Comment.builder()
                .id("cmt_child").userId(USER_B).movieId(MOVIE_ID).parentId("cmt_root")
                .rootId("cmt_root").build();
        when(commentRepository.findById("cmt_child")).thenReturn(Optional.of(grandChildParent));

        service.execute(new CreateCommentCommand(USER_A, MOVIE_SLUG, null, "cmt_child", "Minh thich tap 7"));

        assertThat(savedComment().getRootId())
                .as("rootId phai lay tu parent.getRootId(), khong phai parent.getId()")
                .isEqualTo("cmt_root");
    }

    @Test
    void traLoiBinhLuanCuaPhimKhac_PHAI_bao_loi_va_KHONG_luu() {
        Comment otherMovieComment = Comment.builder()
                .id("cmt_other").userId(USER_B).movieId("mov_khac").build();
        when(commentRepository.findById("cmt_other")).thenReturn(Optional.of(otherMovieComment));

        assertThatThrownBy(() -> service.execute(
                new CreateCommentCommand(USER_A, MOVIE_SLUG, null, "cmt_other", "Lac cho")))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.COMMENT_PARENT_MISMATCH);

        verify(commentRepository, never()).save(any());
    }

    @Test
    void binhLuanCon_keThuaPhamViTap_cuaCha_boQuaGiaTriTrongBody() {
        Comment parentOnEpisode = Comment.builder()
                .id("cmt_ep").userId(USER_B).movieId(MOVIE_ID).episodeId("ep_5").build();
        when(commentRepository.findById("cmt_ep")).thenReturn(Optional.of(parentOnEpisode));

        // Body cố tình gửi ep_99 — phải bị bỏ qua, nếu không sẽ sinh nhánh nằm sai tập.
        service.execute(new CreateCommentCommand(USER_A, MOVIE_SLUG, "ep_99", "cmt_ep", "Tap nay hay"));

        assertThat(savedComment().getEpisodeId()).isEqualTo("ep_5");
        verify(episodeRepository, never()).findById(any());
    }

    @Test
    void traLoiNguoiKhac_PHAI_sinh_dung_mot_thongBao() {
        Comment root = Comment.builder().id("cmt_root").userId(USER_B).movieId(MOVIE_ID).build();
        when(commentRepository.findById("cmt_root")).thenReturn(Optional.of(root));

        service.execute(new CreateCommentCommand(USER_A, MOVIE_SLUG, null, "cmt_root", "Dong y"));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(USER_B);
        assertThat(captor.getValue().getType()).isEqualTo(NotificationType.COMMENT_REPLY);
    }

    @Test
    void tuTraLoiChinhMinh_KHONG_sinh_thongBao() {
        Comment ownComment = Comment.builder().id("cmt_root").userId(USER_A).movieId(MOVIE_ID).build();
        when(commentRepository.findById("cmt_root")).thenReturn(Optional.of(ownComment));

        service.execute(new CreateCommentCommand(USER_A, MOVIE_SLUG, null, "cmt_root", "Bo sung them"));

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void binhLuanGoc_KHONG_sinh_thongBao() {
        service.execute(new CreateCommentCommand(USER_A, MOVIE_SLUG, null, null, "Phim hay"));

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void dangBinhLuan_PHAI_vo_hieu_hoa_cache_cua_phim_do() {
        // Quên bước này thì bình luận vừa đăng không hiện ra cho tới khi cache hết hạn.
        service.execute(new CreateCommentCommand(USER_A, MOVIE_SLUG, null, null, "Phim hay"));

        verify(commentListCache).bumpVersion(MOVIE_ID);
    }
}
