package com.example.backend_pj4.application.usecase.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.port.out.PaymentGatewayPort;
import com.example.backend_pj4.application.port.out.PaymentGatewayPort.CreatePaymentLinkResult;
import com.example.backend_pj4.domain.model.MembershipPlan;
import com.example.backend_pj4.domain.model.PaymentOrder;
import com.example.backend_pj4.domain.repository.MembershipPlanRepository;
import com.example.backend_pj4.domain.repository.PaymentOrderRepository;
import com.example.backend_pj4.infrastructure.config.properties.PayOsProperties;

/**
 * Khoá hành vi sinh mã đơn thanh toán.
 *
 * <p>Mã cũ dùng {@code System.currentTimeMillis() % 1_000_000_000L}: phép chia lấy dư chỉ giữ
 * 9 chữ số cuối, mà 9 chữ số đếm được tối đa 10⁹ mili-giây = 11,57 ngày — cứ 11,57 ngày mã lại
 * lặp lại y hệt. Vì {@code transaction_code} có ràng buộc UNIQUE, lần trùng đó làm khách nhận
 * 500 khi bấm mua gói.
 */
class OrderCodeUniquenessTest {

    private static final String PLAN_SLUG = "goi-thang";

    private PaymentOrderRepository paymentOrderRepository;
    private CreatePaymentOrderService service;
    private final Set<String> seenCodes = ConcurrentHashMap.newKeySet();

    @BeforeEach
    void setUp() {
        MembershipPlanRepository planRepository = mock(MembershipPlanRepository.class);
        paymentOrderRepository = mock(PaymentOrderRepository.class);
        PaymentGatewayPort gatewayPort = mock(PaymentGatewayPort.class);

        PayOsProperties payOsProperties = new PayOsProperties();
        payOsProperties.setPaymentLinkTtlMinutes(15);
        payOsProperties.setReturnUrl("http://localhost/ok");
        payOsProperties.setCancelUrl("http://localhost/cancel");

        lenient().when(planRepository.findBySlug(PLAN_SLUG)).thenReturn(Optional.of(MembershipPlan.builder()
                .id("plan_1")
                .slug(PLAN_SLUG)
                .name("Goi thang")
                .price(new BigDecimal("99000"))
                .durationDays(30)
                .isActive(true)
                .build()));

        // Ghi lại mọi transactionCode đi qua repository — đây là thứ chạm ràng buộc UNIQUE
        lenient().when(paymentOrderRepository.save(any(PaymentOrder.class))).thenAnswer(inv -> {
            PaymentOrder order = inv.getArgument(0);
            if (order.getTransactionCode() != null) {
                seenCodes.add(order.getTransactionCode());
            }
            return order.toBuilder().id("order_" + order.getTransactionCode()).build();
        });

        lenient().when(gatewayPort.createPaymentLink(anyLong(), any(), anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(new CreatePaymentLinkResult("http://pay/x", "link_1"));

        service = new CreatePaymentOrderService(planRepository, paymentOrderRepository, gatewayPort, payOsProperties);
    }

    @Test
    void sinhLienTiep10000Ma_khongCaiNaoTrung() {
        Set<String> codes = new HashSet<>();

        for (int i = 0; i < 10_000; i++) {
            service.execute("usr_1", PLAN_SLUG);
        }

        codes.addAll(seenCodes);
        assertThat(codes)
                .as("10.000 đơn liên tiếp phải ra 10.000 mã khác nhau")
                .hasSize(10_000);
    }

    @Test
    void nhieuLuongTaoDonCungLuc_khongTrungMa() throws Exception {
        int threads = 16;
        int perThread = 200;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                try {
                    startGate.await();
                    for (int i = 0; i < perThread; i++) {
                        service.execute("usr_1", PLAN_SLUG);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        startGate.countDown();
        assertThat(done.await(60, TimeUnit.SECONDS)).isTrue();
        pool.shutdown();

        assertThat(seenCodes)
                .as("hai người bấm mua trong cùng một mili-giây vẫn phải ra hai mã khác nhau")
                .hasSize(threads * perThread);
    }

    @Test
    void maDonPhaiDuChuSo_khongBiCatConLai9ChuSo() {
        service.execute("usr_1", PLAN_SLUG);

        String code = List.copyOf(seenCodes).get(0);
        assertThat(Long.parseLong(code))
                .as("mã bị cắt còn 9 chữ số sẽ lặp lại sau 11,57 ngày")
                .isGreaterThan(1_000_000_000L);
    }
}
