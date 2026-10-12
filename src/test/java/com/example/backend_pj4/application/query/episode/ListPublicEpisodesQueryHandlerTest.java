package com.example.backend_pj4.application.query.episode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.dto.episode.PublicEpisodeResult;
import com.example.backend_pj4.application.service.StorageUrlResolver;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.SortOrder;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.common.pagination.PageQuery;
import com.example.backend_pj4.common.pagination.PageResult;
import com.example.backend_pj4.domain.model.Episode;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.EpisodeRepository;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.filter.EpisodeFilter;

/** Luồng này trước nằm trong {@code PublicEpisodeController} nên chỉ test được qua MockMvc. */
class ListPublicEpisodesQueryHandlerTest {

    private static final PageQuery QUERY = new PageQuery(0, 15, "episodeNumber", SortOrder.desc);

    private MovieRepository movieRepository;
    private EpisodeRepository episodeRepository;
    private ListPublicEpisodesQueryHandler handler;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        episodeRepository = mock(EpisodeRepository.class);
        StorageUrlResolver storageUrlResolver = mock(StorageUrlResolver.class);
        lenient().when(storageUrlResolver.resolvePublicImage(anyString())).thenReturn("http://x/t.jpg");

        handler = new ListPublicEpisodesQueryHandler(movieRepository, episodeRepository, storageUrlResolver);
    }

    private void givenMovie(String slug, String id) {
        when(movieRepository.findBySlug(slug)).thenReturn(Optional.of(Movie.builder().id(id).slug(slug).build()));
        when(episodeRepository.find(any(EpisodeFilter.class), any(PageQuery.class)))
                .thenReturn(new PageResult<>(List.of(
                        Episode.builder().id("ep1").episodeNumber(1).title("Tập 1").thumbnailUrl("t.jpg").build()),
                        1, 1, 15, 1));
    }

    @Test
    void traTapPhimCuaDungBoPhimTheoSlug() {
        givenMovie("co-dau", "mov_1");

        assertThat(handler.execute("co-dau", null, QUERY).items())
                .extracting(PublicEpisodeResult::title)
                .containsExactly("Tập 1");
    }

    @Test
    void slugKhongTonTai_traMOVIE_NOT_FOUND_vaKhongHoiTapPhim() {
        when(movieRepository.findBySlug("khong-co")).thenReturn(Optional.empty());

        try {
            handler.execute("khong-co", null, QUERY);
            throw new AssertionError("mong đợi CustomException");
        } catch (CustomException e) {
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.MOVIE_NOT_FOUND);
        }

        verify(episodeRepository, never()).find(any(EpisodeFilter.class), any(PageQuery.class));
    }

    @Test
    void search_duocDayXuongTANGDUOILIEU() {
        givenMovie("co-dau", "mov_1");

        handler.execute("co-dau", "tap 1", QUERY);

        verify(episodeRepository).find(eq(new EpisodeFilter("mov_1", "tap 1")), any(PageQuery.class));
    }
}
