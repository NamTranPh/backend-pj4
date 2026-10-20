package com.example.backend_pj4.application.usecase.rating;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.backend_pj4.application.port.out.RatingSummaryCache;
import com.example.backend_pj4.application.service.RatingRecalculator;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.RatingRepository;

class DeleteRatingServiceTest {

    private static final String USER = "usr_1";
    private static final String MOVIE_ID = "mov_1";
    private static final String MOVIE_SLUG = "phim-hay";

    private MovieRepository movieRepository;
    private RatingRepository ratingRepository;
    private DeleteRatingService service;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        ratingRepository = mock(RatingRepository.class);
        service = new DeleteRatingService(movieRepository, ratingRepository,
                new RatingRecalculator(ratingRepository, movieRepository, mock(RatingSummaryCache.class)));

        when(movieRepository.findBySlug(MOVIE_SLUG))
                .thenReturn(Optional.of(Movie.builder().id(MOVIE_ID).slug(MOVIE_SLUG).build()));
        when(ratingRepository.findByUserIdAndMovieId(USER, MOVIE_ID))
                .thenReturn(Optional.of(Rating.builder().id("rat_1").userId(USER).movieId(MOVIE_ID).score(5).build()));
    }

    @Test
    void xoaDanhGia_PHAI_xoa_cung() {
        when(ratingRepository.averageScoreByMovieId(MOVIE_ID)).thenReturn(4.0);

        service.execute(USER, MOVIE_SLUG);

        // Xoá cứng chứ không save(deletedAt): bảng rating có UNIQUE(user_id, movie_id) và
        // @Where(deleted_at IS NULL), xoá mềm là người dùng không bao giờ chấm lại được.
        verify(ratingRepository).deleteByUserIdAndMovieId(USER, MOVIE_ID);
        verify(ratingRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void xoaDanhGiaCuoiCung_movieRating_ve_0_chu_khong_null() {
        // Không còn ai chấm thì AVG trả null. Ghi null vào cột rating sẽ làm phim rơi xuống cuối
        // theo cách không đoán được khi client sắp xếp theo điểm.
        when(ratingRepository.averageScoreByMovieId(MOVIE_ID)).thenReturn(null);

        service.execute(USER, MOVIE_SLUG);

        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository).save(captor.capture());
        assertThat(captor.getValue().getRating()).isEqualByComparingTo(new BigDecimal("0.0"));
    }

    @Test
    void chuaTungChamDiem_bao_loi_khongTimThay() {
        when(ratingRepository.findByUserIdAndMovieId(USER, MOVIE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.execute(USER, MOVIE_SLUG))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ErrorCode.RATING_NOT_FOUND);

        verify(ratingRepository, never()).deleteByUserIdAndMovieId(USER, MOVIE_ID);
    }
}
