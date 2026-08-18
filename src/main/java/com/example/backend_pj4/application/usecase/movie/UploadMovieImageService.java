package com.example.backend_pj4.application.usecase.movie;

import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.movie.MovieResult;
import com.example.backend_pj4.application.mapper.MovieResultMapper;
import com.example.backend_pj4.application.port.in.movie.UploadMovieImageUseCase;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;

@Service
public class UploadMovieImageService implements UploadMovieImageUseCase {

    private static final String BUCKET = "movie-public";
    private final MovieRepository movieRepository;
    private final FileStorageService fileStorageService;

    public UploadMovieImageService(MovieRepository movieRepository, FileStorageService fileStorageService) {
        this.movieRepository = movieRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public MovieResult execute(String movieId, String imageType, String filename, InputStream data, long size, String contentType) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        if (!contentType.startsWith("image/")) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }

        String folder = "movies/" + movieId;
        String ext = filename.contains(".") ? filename.substring(filename.lastIndexOf(".")) : ".jpg";
        String objectKey = fileStorageService.upload(BUCKET, folder, imageType + ext, data, size, contentType);
        String url = fileStorageService.getPublicUrl(BUCKET, objectKey);

        Movie.MovieBuilder builder = movie.toBuilder();
        if ("poster".equals(imageType)) {
            builder.posterUrl(url);
        } else if ("backdrop".equals(imageType)) {
            builder.backdropUrl(url);
        }

        Movie saved = movieRepository.save(builder.build());
        return MovieResultMapper.toResult(saved);
    }
}
