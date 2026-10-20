package com.example.backend_pj4.application.query.rating;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.dto.rating.RatingSummaryResult;
import com.example.backend_pj4.application.port.in.rating.GetRatingSummaryQuery;
import com.example.backend_pj4.application.port.out.RatingSummaryCache;
import com.example.backend_pj4.application.service.RatingRecalculator;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;

/**
 * Điểm trung bình và số lượt đánh giá, đọc qua cache.
 *
 * <p>Đây là endpoint hợp với cache nhất trong cả hai module: kết quả giống nhau với mọi người
 * xem, đổi rất thưa (chỉ khi có người chấm điểm), mà lại nằm trên trang chi tiết phim nên được
 * gọi mỗi lượt xem.
 */
@Service
public class GetRatingSummaryQueryHandler implements GetRatingSummaryQuery {

    private final MovieRepository movieRepository;
    private final RatingSummaryCache ratingSummaryCache;
    private final RatingRecalculator ratingRecalculator;

    public GetRatingSummaryQueryHandler(MovieRepository movieRepository,
                                        RatingSummaryCache ratingSummaryCache,
                                        RatingRecalculator ratingRecalculator) {
        this.movieRepository = movieRepository;
        this.ratingSummaryCache = ratingSummaryCache;
        this.ratingRecalculator = ratingRecalculator;
    }

    @Override
    @Transactional(readOnly = true)
    public RatingSummaryResult execute(String movieSlug) {
        Movie movie = movieRepository.findBySlug(movieSlug)
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        return ratingSummaryCache.get(movie.getId())
                .orElseGet(() -> {
                    RatingSummaryResult fresh = ratingRecalculator.summaryOf(movie.getId());
                    ratingSummaryCache.put(movie.getId(), fresh);
                    return fresh;
                });
    }
}
