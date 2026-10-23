package com.example.backend_pj4.infrastructure.database.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend_pj4.infrastructure.database.entities.PaymentOrderJpaEntity;

public interface PaymentOrderJpaRepository extends JpaRepository<PaymentOrderJpaEntity, String> {

    Optional<PaymentOrderJpaEntity> findByTransactionCode(String transactionCode);

    Page<PaymentOrderJpaEntity> findByUser_Id(String userId, Pageable pageable);

    @Query("SELECT p FROM PaymentOrderJpaEntity p WHERE p.gateway = :gateway AND p.status = 'PENDING' "
            + "AND p.createdAt >= :since ORDER BY p.createdAt ASC")
    List<PaymentOrderJpaEntity> findPendingByGatewaySince(@Param("gateway") String gateway,
                                                          @Param("since") LocalDateTime since,
                                                          @Param("limit") int limit);

    /**
     * Giành quyền quyết toán một đơn bằng một câu UPDATE nguyên tử.
     *
     * <p>Webhook của PayOS và {@code PaymentReconcileJob} có thể chạm cùng một đơn cùng lúc.
     * Kiểu đọc status rồi mới ghi (check-then-act) để lọt cả hai luồng qua bước đọc trước khi ai
     * kịp ghi, dẫn tới kích hoạt gói hai lần. Mệnh đề {@code AND status = 'PENDING'} khiến
     * database làm trọng tài: chỉ một luồng nhận được 1, luồng kia nhận 0.
     *
     * @return số dòng cập nhật, 1 nghĩa là luồng này thắng, 0 nghĩa là luồng khác đã xử lý
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE PaymentOrderJpaEntity p SET p.status = 'COMPLETED', p.completedAt = :now, "
            + "p.gatewayTransactionId = COALESCE(:txId, p.gatewayTransactionId) "
            + "WHERE p.id = :id AND p.status = 'PENDING'")
    int markCompletedIfPending(@Param("id") String id,
                               @Param("txId") String gatewayTransactionId,
                               @Param("now") LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE PaymentOrderJpaEntity p SET p.status = 'FAILED', p.failedAt = :now, "
            + "p.failureReason = :reason WHERE p.id = :id AND p.status = 'PENDING'")
    int markFailedIfPending(@Param("id") String id,
                            @Param("reason") String reason,
                            @Param("now") LocalDateTime now);
}
