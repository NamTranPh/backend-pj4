package com.example.backend_pj4.infrastructure.database.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend_pj4.infrastructure.database.entities.MembershipJpaEntity;

@Repository
public interface MembershipJpaRepository extends JpaRepository<MembershipJpaEntity, String> {

    @Query("SELECT m FROM MembershipJpaEntity m WHERE m.user.id = ?1 AND m.isActive = true AND m.endDate >= ?2")
    Optional<MembershipJpaEntity> findActiveMembershipByUserId(String userId, LocalDate currentDate);

    /**
     * Đăng ký còn hiệu lực thuộc một gói.
     *
     * <p>Trước đây adapter gọi findByIsActiveTrue() rồi lọc planId bằng Java, tức là kéo toàn bộ
     * đăng ký đang hoạt động về chỉ để giữ lại vài dòng.
     */
    List<MembershipJpaEntity> findByIsActiveTrueAndPlan_Id(String planId);
}
