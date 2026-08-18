package com.example.backend_pj4.application.port.in.movie;

import java.util.List;

import com.example.backend_pj4.application.dto.movie.PresignedUrlResult;

public interface GetPartUrlsUseCase {
    List<PresignedUrlResult> execute(String sessionId, List<Integer> partNumbers);
}
