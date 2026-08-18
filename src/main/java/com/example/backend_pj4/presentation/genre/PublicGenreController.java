package com.example.backend_pj4.presentation.genre;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.dto.genre.GenreResult;
import com.example.backend_pj4.application.port.in.genre.ListGenresUseCase;

@RestController
@RequestMapping("/v1/genres")
public class PublicGenreController {

    private final ListGenresUseCase listGenresUseCase;

    public PublicGenreController(ListGenresUseCase listGenresUseCase) {
        this.listGenresUseCase = listGenresUseCase;
    }

    @GetMapping
    public Page<GenreResult> list(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return listGenresUseCase.execute(search, Math.max(0, page - 1), limit, true);
    }
}
