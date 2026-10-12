package com.example.backend_pj4.presentation.episode;

import com.example.backend_pj4.common.pagination.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.episode.PublicEpisodeResult;
import com.example.backend_pj4.application.port.in.episode.ListPublicEpisodesQuery;
import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Episodes - Public")
@RestController
@RequestMapping("/api/v1/movies/{movieSlug}/episodes")
public class PublicEpisodeController {

    private final ListPublicEpisodesQuery listPublicEpisodesQuery;

    public PublicEpisodeController(ListPublicEpisodesQuery listPublicEpisodesQuery) {
        this.listPublicEpisodesQuery = listPublicEpisodesQuery;
    }

    @Operation(summary = "Danh sách tập phim công khai.")
    @GetMapping
    public PageResult<PublicEpisodeResult> listEpisodes(
            @PathVariable String movieSlug,
            @ModelAttribute PageQueryParamDto params
    ) {
        return listPublicEpisodesQuery.execute(
                movieSlug,
                params.search(),
                PageableFactory.create(params,
                        ListPublicEpisodesQuery.SORTABLE, ListPublicEpisodesQuery.DEFAULT_SORT));
    }
}
