package com.example.backend_pj4.application.query.rating;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.dto.rating.MyRatingResult;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.domain.repository.filter.RatingFilter;

class ListMyRatingsQueryHandlerTest {

    private RatingRepository ratingRepository;
    private MovieRepository movieRepository;
    private StorageUrlResolver storageUrlResolver;
    private ListMyRatingsQueryHandler handler;

    @BeforeEach
    void setUp() {
        ratingRepository = mock(RatingRepository.class);
        movieRepository = mock(MovieRepository.class);
        storageUrlResolver = mock(StorageUrlResolver.class);
        handler = new ListMyRatingsQueryHandler(ratingRepository, movieRepository, storageUrlResolver);
    }

    @Test
    void execute_traVeDanhSachDanhGiaCuaUser() {
        String userId = "usr_1";
        PageQuery query = new PageQuery(0, 10, "createdAt", SortOrder.desc);

        Rating r1 = Rating.builder()
                .id("rate_1")
                .userId(userId)
                .movieId("mov_1")
                .score(5)
                .review("Phim rat hay")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(ratingRepository.find(any(RatingFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(r1), 1L, 1, 10, 1));

        Movie m1 = Movie.builder()
                .id("mov_1")
                .slug("phim-1")
                .title("Phim Mot")
                .posterUrl("posters/m1.jpg")
                .build();
        when(movieRepository.findByIds(anyCollection())).thenReturn(List.of(m1));
        when(storageUrlResolver.resolvePublicImage("posters/m1.jpg")).thenReturn("http://cdn/posters/m1.jpg");

        PageResult<MyRatingResult> result = handler.execute(userId, query);

        assertThat(result.items()).hasSize(1);
        MyRatingResult item = result.items().get(0);
        assertThat(item.id()).isEqualTo("rate_1");
        assertThat(item.movieSlug()).isEqualTo("phim-1");
        assertThat(item.movieTitle()).isEqualTo("Phim Mot");
        assertThat(item.posterUrl()).isEqualTo("http://cdn/posters/m1.jpg");
        assertThat(item.score()).isEqualTo(5);
        assertThat(item.review()).isEqualTo("Phim rat hay");
    }

    @Test
    void execute_khiKhongCoDanhGia_traVeRong() {
        String userId = "usr_1";
        PageQuery query = new PageQuery(0, 10, "createdAt", SortOrder.desc);

        when(ratingRepository.find(any(RatingFilter.class), any(PageQuery.class)))
                .thenReturn(PageResult.empty(query));

        PageResult<MyRatingResult> result = handler.execute(userId, query);

        assertThat(result.items()).isEmpty();
    }
}
