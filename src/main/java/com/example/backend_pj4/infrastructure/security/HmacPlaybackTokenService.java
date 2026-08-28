// Tạo và xác thực Playback Token bằng HMAC-SHA256
// Nói đơn giản là tạo ra một token có dạng: payload.signature
// Token này dùng để chứng minh rằng người dùng được phép xem một content cụ thể, đồng thời token có thời gian hết hạn.
package com.example.backend_pj4.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.PlaybackTokenService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.infrastructure.config.properties.PlaybackProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HmacPlaybackTokenService implements PlaybackTokenService {

    private final PlaybackProperties properties;
    private final ObjectMapper objectMapper;

    public HmacPlaybackTokenService(PlaybackProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public String issueToken(String contentId, String contentType, String userId) {
        try {
            long exp = System.currentTimeMillis() / 1000 + properties.getTokenTtlSeconds();
            String payload = objectMapper.writeValueAsString(
                    java.util.Map.of("contentId", contentId, "contentType", contentType, "userId", userId, "exp", exp));

            String payloadB64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
            String sig = hmac(payloadB64);
            return payloadB64 + "." + sig;
        } catch (Exception e) {
            throw new RuntimeException("Failed to issue playback token", e);
        }
    }

    @Override
    public PlaybackTokenPayload verifyToken(String token) {
        try {
            log.debug("Verifying playback token, length={}", token.length());
            String[] parts = token.split("\\.", 2);
            if (parts.length != 2) {
                log.warn("Playback token split failed, parts={}", parts.length);
                throw new CustomException(ErrorCode.PLAYBACK_TOKEN_INVALID);
            }

            String payloadB64 = parts[0];
            String sig = parts[1];

            String expectedSig = hmac(payloadB64);
            if (!expectedSig.equals(sig)) {
                log.warn("Playback token HMAC mismatch: expected={}, got={}", expectedSig, sig);
                throw new CustomException(ErrorCode.PLAYBACK_TOKEN_INVALID);
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(payloadB64), StandardCharsets.UTF_8);
            JsonNode node = objectMapper.readTree(payloadJson);

            long exp = node.get("exp").asLong();
            if (System.currentTimeMillis() / 1000 > exp) {
                log.warn("Playback token expired: exp={}, now={}", exp, System.currentTimeMillis() / 1000);
                throw new CustomException(ErrorCode.PLAYBACK_TOKEN_INVALID);
            }

            return new PlaybackTokenPayload(
                    node.get("contentId").asText(),
                    node.get("contentType").asText(),
                    node.get("userId").asText()
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.PLAYBACK_TOKEN_INVALID);
        }
    }

    private String hmac(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    properties.getTokenSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC computation failed", e);
        }
    }
}
