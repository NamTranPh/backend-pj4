package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.HistoryWatchingJpaEntity;

public interface HistoryWatchingJpaRepository extends JpaRepository<HistoryWatchingJpaEntity, String> {

    /**
     * Lịch sử xem của một người, kèm lọc theo tên phim và lọc phim đang xem dở.
     *
     * <p>Bảng history chỉ lưu movieId nên tìm theo tên phim phải JOIN sang movie.
     *
     * <p>Điều kiện isCompleted phải nằm TRONG query. Bản cũ lấy top 10 rồi mới lọc bằng Java,
     * nên nếu 5/10 dòng đã xem xong thì chỉ còn 5 kết quả: mỗi trang trả về số lượng khác nhau
     * và totalElements sai.
     */
    @Query("SELECT h FROM HistoryWatchingJpaEntity h WHERE h.user.id = :userId "
            + "AND (:search IS NULL OR :search = '' "
            + "     OR LOWER(h.movie.title) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "AND (:unfinishedOnly = false OR h.isCompleted IS NULL OR h.isCompleted = false)")
    Page<HistoryWatchingJpaEntity> search(@Param("userId") String userId,
                                          @Param("search") String search,
                                          @Param("unfinishedOnly") boolean unfinishedOnly,
                                          Pageable pageable);

    Optional<HistoryWatchingJpaEntity> findByUser_IdAndMovie_IdAndEpisodeIsNull(String userId, String movieId);

    Optional<HistoryWatchingJpaEntity> findByUser_IdAndMovie_Id(String userId, String movieId);

    Optional<HistoryWatchingJpaEntity> findByUser_IdAndMovie_IdAndEpisode_Id(
            String userId, String movieId, String episodeId);

    @Modifying
    @Query("DELETE FROM HistoryWatchingJpaEntity h WHERE h.user.id = :userId")
    void deleteByUserId(@Param("userId") String userId);
}
