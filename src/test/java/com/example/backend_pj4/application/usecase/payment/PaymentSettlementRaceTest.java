package com.example.backend_pj4.application.usecase.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.common.constants.enums.PaymentOrderStatus;
import com.example.backend_pj4.domain.model.Membership;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.MembershipRepository;
import com.example.backend_pj4.domain.repository.NotificationRepository;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;

/**
 * Khoá hành vi quyết toán khi webhook PayOS và {@code PaymentReconcileJob} chạm cùng một đơn
 * cùng lúc.
 *
 * <p>Chốt chặn cũ là {@code if (order.getStatus() == COMPLETED) return} — đọc rồi mới ghi.
 * Hai luồng cùng lọt qua bước đọc trước khi ai kịp ghi, nên {@code activateMembership} chạy
 * hai lần: khách trả tiền một gói 30 ngày nhưng nhận 60 ngày.
 *
 * <p>Ở đây {@code markCompletedIfPending} được giả lập đúng ngữ nghĩa của câu
 * {@code UPDATE ... WHERE status = 'PENDING'}: chỉ lần gọi đầu tiên trả về 1.
 */
class PaymentSettlementRaceTest {

    private static final String ORDER_ID = "order_1";
    private static final String USER_ID = "usr_1";

    private PaymentOrderRepository paymentOrderRepository;
    private MembershipRepository membershipRepository;
    private PaymentSettlementService service;

    /** Đếm số lần membership thực sự được tạo/gia hạn. */
    private AtomicInteger membershipWrites;

    private PaymentOrder pendingOrder() {
        return PaymentOrder.builder()
                .id(ORDER_ID)
                .userId(USER_ID)
                .membershipPlanId("plan_1")
                .planName("Goi thang")
                .durationDays(30)
                .status(PaymentOrderStatus.PENDING)
                .transactionCode("123456789012")
                .build();
    }

    @BeforeEach
    void setUp() {
        paymentOrderRepository = mock(PaymentOrderRepository.class);
        membershipRepository = mock(MembershipRepository.class);
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        membershipWrites = new AtomicInteger();

        // Giả lập UPDATE ... WHERE status='PENDING': chỉ luồng đầu tiên nhận được 1
        AtomicInteger winner = new AtomicInteger();
        lenient().when(paymentOrderRepository.markCompletedIfPending(eq(ORDER_ID), any(), any()))
                .thenAnswer(inv -> winner.incrementAndGet() == 1 ? 1 : 0);
        lenient().when(paymentOrderRepository.markFailedIfPending(eq(ORDER_ID), anyString(), any()))
                .thenAnswer(inv -> winner.incrementAndGet() == 1 ? 1 : 0);

        lenient().when(membershipRepository.findActiveByUserId(USER_ID)).thenReturn(Optional.empty());
        lenient().when(membershipRepository.save(any(Membership.class))).thenAnswer(inv -> {
            membershipWrites.incrementAndGet();
            return inv.getArgument(0);
        });
        lenient().when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service = new PaymentSettlementService(
                paymentOrderRepository, membershipRepository, notificationRepository);
    }

    @Test
    void webhookVaJobChayDongThoi_chiKichHoatGoiMOTLan() throws Exception {
        PaymentOrder order = pendingOrder();

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);
        AtomicInteger wonCount = new AtomicInteger();

        ExecutorService pool = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            pool.submit(() -> {
                try {
                    startGate.await();
                    if (service.completePayment(order, "tx_1")) {
                        wonCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        startGate.countDown();
        assertThat(done.await(10, TimeUnit.SECONDS)).isTrue();
        pool.shutdown();

        assertThat(wonCount.get())
                .as("đúng một luồng được quyết toán đơn")
                .isEqualTo(1);
        assertThat(membershipWrites.get())
                .as("gói chỉ được kích hoạt một lần — chạy hai lần là khách nhận gấp đôi số ngày")
                .isEqualTo(1);
    }

    @Test
    void webhookGuiLaiTuanTu_lanThuHaiBiBoQua() {
        PaymentOrder order = pendingOrder();

        assertThat(service.completePayment(order, "tx_1")).isTrue();
        assertThat(service.completePayment(order, "tx_1")).isFalse();

        assertThat(membershipWrites.get()).isEqualTo(1);
    }

    @Test
    void thatBaiCungChiGhiMotLan() {
        PaymentOrder order = pendingOrder();

        assertThat(service.failPayment(order, "PayOS status: CANCELLED")).isTrue();
        assertThat(service.failPayment(order, "PayOS status: CANCELLED")).isFalse();
    }
}
