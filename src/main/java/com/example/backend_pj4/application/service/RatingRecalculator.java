package com.example.backend_pj4.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.example.backend_pj4.application.dto.rating.RatingSummaryResult;
import com.example.backend_pj4.application.port.out.RatingSummaryCache;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.RatingRepository;

/**
 * Nơi <b>duy nhất</b> ghi vào cột {@code movie.rating}.
 *
 * <p>Gom lại một chỗ vì hai luồng cần nó (chấm điểm và xoá điểm) và cả hai đều phải làm đủ ba
 * việc: tính lại trung bình, ghi vào phim, xoá cache tóm tắt. Thiếu việc thứ ba thì trang chi
 * tiết phim hiện điểm mới còn ô tóm tắt vẫn hiện điểm cũ tới hết TTL.
 *
 * <p>Cố ý <b>không</b> gắn {@code @Transactional}: nó chạy trong transaction của use case gọi
 * nó, để nếu lượt chấm điểm bị rollback thì số trung bình cũng rollback theo. Mở
 * {@code REQUIRES_NEW} ở đây sẽ ghi một con số trung bình tính từ dữ liệu chưa commit
 * (AGENTS.md §12).
 */
@Service
public class RatingRecalculator {

    private final RatingRepository ratingRepository;
    private final MovieRepository movieRepository;
    private final RatingSummaryCache ratingSummaryCache;

    public RatingRecalculator(RatingRepository ratingRepository,
                              MovieRepository movieRepository,
                              RatingSummaryCache ratingSummaryCache) {
        this.ratingRepository = ratingRepository;
        this.movieRepository = movieRepository;
        this.ratingSummaryCache = ratingSummaryCache;
    }

    /**
     * Tính lại trung bình từ bảng {@code rating} rồi ghi vào phim.
     *
     * <p>Luôn tính lại từ dữ liệu gốc chứ không cộng dồn vào con số đã làm tròn — cộng dồn thì
     * sai số tích luỹ vĩnh viễn, còn tính lại thì sai số tối đa luôn là nửa đơn vị làm tròn.
     */
    public void recalculate(Movie movie) {
        Double average = ratingRepository.averageScoreByMovieId(movie.getId());

        // Xoá lượt đánh giá cuối cùng thì AVG trả null. Ghi 0.0 chứ không ghi null: cột rating
        // nằm trong whitelist sắp xếp của danh sách phim, null sẽ làm phim rơi xuống cuối theo
        // cách không ai đoán được, tuỳ chiều sắp xếp.
        BigDecimal value = average == null
                ? BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP);

        movieRepository.save(movie.toBuilder().rating(value).build());
        ratingSummaryCache.evict(movie.getId());
    }

    /** Tóm tắt đọc thẳng từ database, không qua cache — dùng khi vừa ghi xong và cần số mới nhất. */
    public RatingSummaryResult summaryOf(String movieId) {
        Double average = ratingRepository.averageScoreByMovieId(movieId);
        BigDecimal value = average == null
                ? BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP);
        return new RatingSummaryResult(value, ratingRepository.countByMovieId(movieId));
    }
}
