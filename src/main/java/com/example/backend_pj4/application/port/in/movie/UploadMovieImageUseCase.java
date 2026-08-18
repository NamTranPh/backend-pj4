package com.example.backend_pj4.application.port.in.movie;

import java.io.InputStream;

import com.example.backend_pj4.application.dto.movie.MovieResult;

public interface UploadMovieImageUseCase {
    MovieResult execute(String movieId, String imageType, String filename, InputStream data, long size, String contentType);
}
