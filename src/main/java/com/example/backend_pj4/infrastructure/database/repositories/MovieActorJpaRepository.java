package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.MovieActorJpaEntity;

public interface MovieActorJpaRepository extends JpaRepository<MovieActorJpaEntity, String> {

    List<MovieActorJpaEntity> findByMovieIdOrderByOrderIndexAsc(String movieId);

    @Modifying
    @Query("DELETE FROM MovieActorJpaEntity ma WHERE ma.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") String movieId);
}
