package com.example.backend_pj4.presentation.favorite;


import org.springframework.http.HttpStatus;
import com.example.backend_pj4.common.pagination.PageResult;
import org.springframework.http.ResponseEntity;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.favorite.FavoriteMovieResult;
import com.example.backend_pj4.application.port.in.favorite.AddFavoriteUseCase;
import com.example.backend_pj4.application.port.in.favorite.ListFavoriteMoviesQuery;
import com.example.backend_pj4.domain.repository.filter.FavoriteFilter;
import com.example.backend_pj4.application.port.in.favorite.RemoveFavoriteUseCase;
import com.example.backend_pj4.infrastructure.security.AppUserDetails;

import com.example.backend_pj4.presentation.common.request.PageQueryParamDto;
import com.example.backend_pj4.presentation.common.request.PageableFactory;

import com.example.backend_pj4.common.annotation.AuthRequired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Favorites")
@AuthRequired
@RestController
@RequestMapping("/api/v1/me/favorites")
public class FavoriteController {

    private final AddFavoriteUseCase addFavoriteUseCase;
    private final RemoveFavoriteUseCase removeFavoriteUseCase;
    private final ListFavoriteMoviesQuery listFavoriteMoviesQuery;

    public FavoriteController(
            AddFavoriteUseCase addFavoriteUseCase,
            RemoveFavoriteUseCase removeFavoriteUseCase,
            ListFavoriteMoviesQuery listFavoriteMoviesQuery
    ) {
        this.addFavoriteUseCase = addFavoriteUseCase;
        this.removeFavoriteUseCase = removeFavoriteUseCase;
        this.listFavoriteMoviesQuery = listFavoriteMoviesQuery;
    }

    @Operation(summary = "Thêm phim vào danh sách yêu thích.")
    @PostMapping("/{movieSlug}")
    public ResponseEntity<Void> add(
            @PathVariable String movieSlug,
            @AuthResultUser UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        addFavoriteUseCase.execute(userId, movieSlug);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Xóa phim khỏi danh sách yêu thích.")
    @DeleteMapping("/{movieSlug}")
    public ResponseEntity<Void> remove(
            @PathVariable String movieSlug,
            @AuthResultUser UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        removeFavoriteUseCase.execute(userId, movieSlug);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Lấy danh sách phim yêu thích.")
    @GetMapping
    public PageResult<FavoriteMovieResult> list(
            @AuthResultUser UserDetails userDetails,
            @ModelAttribute PageQueryParamDto params) {
        return listFavoriteMoviesQuery.execute(
                new FavoriteFilter(resolveUserId(userDetails), params.search()),
                PageableFactory.create(params,
                        ListFavoriteMoviesQuery.SORTABLE, ListFavoriteMoviesQuery.DEFAULT_SORT));
    }

    /** userId lấy thẳng từ principal — filter đã nạp sẵn, không cần tra lại database. */
    private String resolveUserId(UserDetails userDetails) {
        return AppUserDetails.userIdOf(userDetails);
    }
}
