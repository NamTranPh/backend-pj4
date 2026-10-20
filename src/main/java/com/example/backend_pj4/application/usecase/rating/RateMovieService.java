package com.example.backend_pj4.application.usecase.rating;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend_pj4.application.command.rating.RateMovieCommand;
import com.example.backend_pj4.application.dto.rating.RatingResult;
import com.example.backend_pj4.application.mapper.RatingResultMapper;
import com.example.backend_pj4.application.port.in.rating.RateMovieUseCase;
import com.example.backend_pj4.application.service.RatingRecalculator;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.model.Movie;
import com.example.backend_pj4.domain.model.Rating;
import com.example.backend_pj4.domain.repository.MovieRepository;
import com.example.backend_pj4.domain.repository.RatingRepository;
import com.example.backend_pj4.domain.repository.UserRepository;

/**
 * Chấm sao cho phim — tạo mới hoặc ghi đè, mỗi người một phim một đánh giá.
 */
@Service
public class RateMovieService implements RateMovieUseCase {

    /** Thang điểm là số sao, số nguyên. Đổi thang thì sửa cả ở RateMovieRequest. */
    private static final int MIN_SCORE = 1;
    private static final int MAX_SCORE = 5;

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final RatingResultMapper ratingResultMapper;
    private final RatingRecalculator ratingRecalculator;

    public RateMovieService(MovieRepository movieRepository,
                            RatingRepository ratingRepository,
                            UserRepository userRepository,
                            RatingResultMapper ratingResultMapper,
                            RatingRecalculator ratingRecalculator) {
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.ratingResultMapper = ratingResultMapper;
        this.ratingRecalculator = ratingRecalculator;
    }

    @Override
    @Transactional
    public RatingResult execute(RateMovieCommand command) {
        // Lớp phòng thủ thứ hai sau @Min/@Max ở DTO: điểm số đi thẳng vào phép tính trung bình
        // hiển thị công khai, đây là chỗ đáng giữ cả hai lớp kiểm tra (AGENTS.md §22).
        if (command.score() == null || command.score() < MIN_SCORE || command.score() > MAX_SCORE) {
            throw new CustomException(ErrorCode.RATING_SCORE_INVALID);
        }

        Movie movie = movieRepository.findBySlug(command.movieSlug())
                .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        // Chấm lại thì sửa đúng dòng cũ. INSERT dòng mới sẽ đụng UNIQUE(user_id, movie_id).
        Rating rating = ratingRepository.findByUserIdAndMovieId(command.userId(), movie.getId())
                .map(existing -> existing.toBuilder()
                        .score(command.score())
                        .review(command.review())
                        .build())
                .orElseGet(() -> Rating.builder()
                        .userId(command.userId())
                        .movieId(movie.getId())
                        .score(command.score())
                        .review(command.review())
                        .build());

        Rating saved = ratingRepository.save(rating);

        // Trong cùng transaction: điểm trung bình không được phép lệch với bảng rating, kể cả
        // trong khoảnh khắc giữa hai lệnh ghi.
        ratingRecalculator.recalculate(movie);

        return ratingResultMapper.toResult(saved,
                userRepository.findById(command.userId()).orElse(null));
    }
}
