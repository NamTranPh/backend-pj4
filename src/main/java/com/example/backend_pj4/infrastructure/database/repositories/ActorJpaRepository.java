package com.example.backend_pj4.infrastructure.database.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.ActorJpaEntity;

public interface ActorJpaRepository extends JpaRepository<ActorJpaEntity, String> {

    @Query("SELECT a FROM ActorJpaEntity a WHERE (:search IS NULL OR :search = '' "
            + "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(a.originalName) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "AND (:nationality IS NULL OR :nationality = '' OR LOWER(a.nationality) = LOWER(:nationality))")
    Page<ActorJpaEntity> search(@Param("search") String search,
                                @Param("nationality") String nationality,
                                Pageable pageable);

    boolean existsByName(String name);
}
