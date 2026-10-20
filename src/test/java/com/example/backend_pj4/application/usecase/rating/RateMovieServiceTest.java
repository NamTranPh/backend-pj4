package com.example.backend_pj4.application.usecase.rating;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.backend_pj4.application.command.rating.RateMovieCommand;
import com.example.backend_pj4.application.mapper.CommentResultMapper;
import com.example.backend_pj4.application.mapper.RatingResultMapper;
import com.example.backend_pj4.application.port.out.RatingSummaryCache;
import com.example.backend_pj4.application.service.RatingRecalculator;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

/**
 * Hai thứ dễ hỏng ở luồng chấm sao:
 *
 * <ul>
 *   <li><b>Chấm lại phải SỬA dòng cũ, không INSERT dòng mới</b> — bảng {@code rating} có
 *       {@code UNIQUE(user_id, movie_id)}, INSERT sẽ ném lỗi ràng buộc thẳng ra người dùng.</li>
 *   <li><b>{@code movie.rating} phải tính lại từ bảng rating</b>, không cộng dồn vào con số đã
 *       làm tròn — cộng dồn thì sai số tích luỹ mãi mãi.</li>
 * </ul>
 */
class RateMovieServiceTest {

    private static final String USER = "usr_1";
    private static final String MOVIE_ID = "mov_1";
    private static final String MOVIE_SLUG = "phim-hay";

    private MovieRepository movieRepository;
    private RatingRepository ratingRepository;
    private RatingSummaryCache ratingSummaryCache;
    private RateMovieService service;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        ratingRepository = mock(RatingRepository.class);
        ratingSummaryCache = mock(RatingSummaryCache.class);

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        RatingResultMapper ratingResultMapper =
                new RatingResultMapper(new CommentResultMapper(mock(StorageUrlResolver.class)));

        service = new RateMovieService(movieRepository, ratingRepository, userRepository,
                ratingResultMapper,
                new RatingRecalculator(ratingRepository, movieRepository, ratingSummaryCache));

        when(movieRepository.findBySlug(MOVIE_SLUG))
                .thenReturn(Optional.of(Movie.builder().id(MOVIE_ID).slug(MOVIE_SLUG).build()));
        when(ratingRepository.save(any(Rating.class)))
                .thenAnswer(inv -> inv.getArgument(0, Rating.class).toBuilder().id("rat_1").build());
        when(ratingRepository.countByMovieId(MOVIE_ID)).thenReturn(1L);
    }

    private Rating savedRating() {
        ArgumentCaptor<Rating> captor = ArgumentCaptor.forClass(Rating.class);
        verify(ratingRepository).save(captor.capture());
        return captor.getValue();
    }

    private BigDecimal savedMovieRating() {
        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository).save(captor.capture());
        return captor.getValue().getRating();
    }

    @Test
    void chamLanDau_tao_dong_moi() {
        when(ratingRepository.findByUserIdAndMovieId(USER, MOVIE_ID)).thenReturn(Optional.empty());
        when(ratingRepository.averageScoreByMovieId(MOVIE_ID)).thenReturn(5.0);

        service.execute(new RateMovieCommand(USER, MOVIE_SLUG, 5, "Qua hay"));

        Rating saved = savedRating();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getScore()).isEqualTo(5);
        assertThat(saved.getUserId()).isEqualTo(USER);
    }

    @Test
    void chamLai_PHAI_sua_dong_cu_chu_khong_tao_dong_moi() {
        Rating existing = Rating.builder()
                .id("rat_cu").userId(USER).movieId(MOVIE_ID).score(5).review("Cu").build();
        when(ratingRepository.findByUserIdAndMovieId(USER, MOVIE_ID)).thenReturn(Optional.of(existing));
        when(ratingRepository.averageScoreByMovieId(MOVIE_ID)).thenReturn(2.0);

        service.execute(new RateMovieCommand(USER, MOVIE_SLUG, 2, "Xem lai thay chan"));

        Rating saved = savedRating();
        // Giữ nguyên id nghĩa là UPDATE, không đụng UNIQUE(user_id, movie_id).
        assertThat(saved.getId()).isEqualTo("rat_cu");
        assertThat(saved.getScore()).isEqualTo(2);
        assertThat(saved.getReview()).isEqualTo("Xem lai thay chan");
    }

    @Test
    void diemTrungBinh_lam_tron_mot_chu_so_thap_phan() {
        when(ratingRepository.findByUserIdAndMovieId(USER, MOVIE_ID)).thenReturn(Optional.empty());
        // 5, 4, 2 -> 3.666...
        when(ratingRepository.averageScoreByMovieId(MOVIE_ID)).thenReturn(3.6666666);

        service.execute(new RateMovieCommand(USER, MOVIE_SLUG, 2, null));

        assertThat(savedMovieRating()).isEqualByComparingTo(new BigDecimal("3.7"));
    }

    @Test
    void chamXong_PHAI_xoa_cache_tomTat() {
        when(ratingRepository.findByUserIdAndMovieId(USER, MOVIE_ID)).thenReturn(Optional.empty());
        when(ratingRepository.averageScoreByMovieId(MOVIE_ID)).thenReturn(4.0);

        service.execute(new RateMovieCommand(USER, MOVIE_SLUG, 4, null));

        verify(ratingSummaryCache).evict(MOVIE_ID);
    }

    @Test
    void diemNgoaiThang_1_5_bi_tu_choi() {
        for (Integer score : new Integer[]{0, 6, -1, null}) {
            assertThatThrownBy(() -> service.execute(new RateMovieCommand(USER, MOVIE_SLUG, score, null)))
                    .isInstanceOf(CustomException.class)
                    .extracting(e -> ((CustomException) e).getErrorCode())
                    .isEqualTo(ErrorCode.RATING_SCORE_INVALID);
        }

        verify(ratingRepository, never()).save(any());
    }

    @Test
    void phimKhongTonTai_bao_loi_truoc_khi_ghi() {
        when(movieRepository.findBySlug("khong-co")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(new RateMovieCommand(USER, "khong-co", 5, null)))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.MOVIE_NOT_FOUND);

        verify(ratingRepository, never()).save(any());
    }
}
