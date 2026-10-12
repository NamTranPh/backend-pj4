package com.example.backend_pj4.infrastructure.database.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.infrastructure.database.entities.MembershipPlanJpaEntity;

@Repository
public interface MembershipPlanJpaRepository extends JpaRepository<MembershipPlanJpaEntity, String> {

    /** Tìm theo tên gói, kèm lọc chỉ gói đang bật. Tham số null thì bỏ qua điều kiện đó. */
    @Query("SELECT p FROM MembershipPlanJpaEntity p WHERE (:search IS NULL OR :search = '' "
            + "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "AND (:activeOnly = false OR p.isActive = true)")
    Page<MembershipPlanJpaEntity> search(@Param("search") String search,
                                         @Param("activeOnly") boolean activeOnly,
                                         Pageable pageable);

    boolean existsByName(String name);

    Optional<MembershipPlanJpaEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);
}


