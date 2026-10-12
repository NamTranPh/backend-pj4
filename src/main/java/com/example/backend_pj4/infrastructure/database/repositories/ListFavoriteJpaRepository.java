package com.example.backend_pj4.infrastructure.database.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.ListFavoriteJpaEntity;

public interface ListFavoriteJpaRepository extends JpaRepository<ListFavoriteJpaEntity, String> {

    /**
     * Phim yeu thich, tim theo TEN PHIM.
     *
     * <p>Bang favorite chi luu movieId nen phai JOIN sang movie moi loc duoc theo ten.
     * {@code f.movie.title} tao implicit inner join - favorite luon co FK toi movie.
     */
    @Query("SELECT f FROM ListFavoriteJpaEntity f WHERE f.user.id = :userId "
            + "AND (:search IS NULL OR :search = '' "
            + "     OR LOWER(f.movie.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ListFavoriteJpaEntity> searchByUserId(@Param("userId") String userId,
                                               @Param("search") String search,
                                               Pageable pageable);
    boolean existsByUser_IdAndMovie_Id(String userId, String movieId);
    @Modifying
    @Query("DELETE FROM ListFavoriteJpaEntity f WHERE f.user.id = :userId AND f.movie.id = :movieId")
    void deleteByUserIdAndMovieId(@Param("userId") String userId, @Param("movieId") String movieId);
}


