package com.example.backend_pj4.infrastructure.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.backend_pj4.infrastructure.config.properties.PayOsProperties;
import com.example.backend_pj4.infrastructure.config.properties.PlaybackProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Cảnh báo khi cấu hình của một tính năng chưa đầy đủ.
 *
 * <p><b>Chỉ ghi log, KHÔNG chặn khởi động.</b> Thiếu khoá PayOS thì chỉ luồng thanh toán không
 * dùng được; đăng nhập, xem phim, upload vẫn phải chạy bình thường. Chặn khởi động toàn cục vì
 * một tính năng chưa cấu hình là quá tay.
 *
 * <p>Lỗi thật sẽ hiện ra ở chỗ dùng: {@code PayOsPaymentGatewayAdapter.assertConfigured()} và
 * {@code HmacPlaybackTokenService.hmac()} đều ném lỗi có tên rõ ràng kèm log, thay vì
 * {@code NullPointerException} bị nuốt thành một mã lỗi chung chung.
 *
 * <p>Mục đích của class này là để lúc khởi động đã biết trước tính năng nào sẽ hỏng, không phải
 * đợi người dùng thật bấm vào mới phát hiện.
 */
@Slf4j
@Component
public class ConfigurationWarningLogger implements ApplicationRunner {

    private final PayOsProperties payOsProperties;
    private final PlaybackProperties playbackProperties;

    public ConfigurationWarningLogger(PayOsProperties payOsProperties,
                                      PlaybackProperties playbackProperties) {
        this.payOsProperties = payOsProperties;
        this.playbackProperties = playbackProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        warnPayOs();
        warnPlayback();
    }

    private void warnPayOs() {
        List<String> missing = new ArrayList<>();
        if (!StringUtils.hasText(payOsProperties.getClientId())) missing.add("PAYOS_CLIENT_ID");
        if (!StringUtils.hasText(payOsProperties.getApiKey())) missing.add("PAYOS_API_KEY");
        if (!StringUtils.hasText(payOsProperties.getChecksumKey())) missing.add("PAYOS_CHECKSUM_KEY");
        if (!StringUtils.hasText(payOsProperties.getBaseUrl())) missing.add("PAYOS_BASE_URL");

        if (!missing.isEmpty()) {
            log.warn("PayOS chưa cấu hình, thiếu {} — luồng thanh toán sẽ trả lỗi "
                    + "payment_gateway_not_configured. Các tính năng khác không ảnh hưởng.", missing);
        }
    }

    private void warnPlayback() {
        if (!StringUtils.hasText(playbackProperties.getTokenSecret())) {
            log.warn("Chưa cấu hình PLAYBACK_TOKEN_SECRET — API phát video sẽ không ký được token. "
                    + "Các tính năng khác không ảnh hưởng.");
        }
    }
}
