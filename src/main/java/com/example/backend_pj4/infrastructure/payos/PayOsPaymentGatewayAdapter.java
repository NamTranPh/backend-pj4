package com.example.backend_pj4.infrastructure.payos;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.backend_pj4.application.port.out.PaymentGatewayPort;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.infrastructure.config.properties.PayOsProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PayOsPaymentGatewayAdapter implements PaymentGatewayPort {

    private final PayOsProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PayOsPaymentGatewayAdapter(PayOsProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    @Override
    public CreatePaymentLinkResult createPaymentLink(long orderCode, BigDecimal amount, String description,
                                                      String returnUrl, String cancelUrl, Long expiredAt) {
        try {
            int amountInt = amount.intValue();

            String dataToSign = "amount=" + amountInt
                    + "&cancelUrl=" + cancelUrl
                    + "&description=" + description
                    + "&orderCode=" + orderCode
                    + "&returnUrl=" + returnUrl;
            String signature = hmacSha256(properties.getChecksumKey(), dataToSign);

            Map<String, Object> body = new HashMap<>();
            body.put("orderCode", orderCode);
            body.put("amount", amountInt);
            body.put("description", description);
            body.put("returnUrl", returnUrl);
            body.put("cancelUrl", cancelUrl);
            body.put("signature", signature);
            if (expiredAt != null) {
                body.put("expiredAt", expiredAt);
            }

            String response = restClient.post()
                    .uri("/v2/payment-requests")
                    .header("x-client-id", properties.getClientId())
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.get("data");
            if (data == null || !root.has("code") || !"00".equals(root.get("code").asText())) {
                log.error("PayOS create payment link failed: {}", response);
                throw new CustomException(ErrorCode.PAYMENT_CREATION_FAILED);
            }

            return new CreatePaymentLinkResult(
                    data.get("checkoutUrl").asText(),
                    data.has("paymentLinkId") ? data.get("paymentLinkId").asText() : null
            );
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("PayOS create payment link error", e);
            throw new CustomException(ErrorCode.PAYMENT_CREATION_FAILED);
        }
    }

    @Override
    public WebhookPayload verifyWebhookData(String webhookBody) {
        try {
            JsonNode root = objectMapper.readTree(webhookBody);
            JsonNode data = root.get("data");
            if (data == null) {
                throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
            }

            String receivedSignature = root.has("signature") ? root.get("signature").asText() : "";

            TreeMap<String, String> sortedData = new TreeMap<>();
            data.fields().forEachRemaining(entry ->
                    sortedData.put(entry.getKey(), entry.getValue().asText()));

            StringBuilder sb = new StringBuilder();
            sortedData.forEach((k, v) -> {
                if (sb.length() > 0) sb.append("&");
                sb.append(k).append("=").append(v);
            });

            String computedSignature = hmacSha256(properties.getChecksumKey(), sb.toString());
            if (!computedSignature.equals(receivedSignature)) {
                log.warn("PayOS webhook signature mismatch");
                throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
            }

            long orderCode = data.get("orderCode").asLong();
            String status = data.has("code") ? data.get("code").asText() : "00";
            String transactionId = data.has("reference") ? data.get("reference").asText() : null;

            String resolvedStatus = "00".equals(status) ? "PAID" : "FAILED";

            return new WebhookPayload(orderCode, resolvedStatus, transactionId);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("PayOS webhook verification error", e);
            throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
        }
    }

    @Override
    public PaymentStatusResult getPaymentStatus(String paymentLinkId) {
        try {
            String response = restClient.get()
                    .uri("/v2/payment-requests/{id}", paymentLinkId)
                    .header("x-client-id", properties.getClientId())
                    .header("x-api-key", properties.getApiKey())
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.get("data");
            if (data == null || !root.has("code") || !"00".equals(root.get("code").asText())) {
                log.error("PayOS get payment status failed: {}", response);
                throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
            }

            String status = data.get("status").asText();
            long orderCode = data.get("orderCode").asLong();
            BigDecimal amount = data.has("amount") ? new BigDecimal(data.get("amount").asText()) : BigDecimal.ZERO;

            return new PaymentStatusResult(status, orderCode, amount);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("PayOS get payment status error for paymentLinkId={}", paymentLinkId, e);
            throw new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
        }
    }

    @Override
    public void cancelPaymentLink(String paymentLinkId) {
        try {
            String response = restClient.post()
                    .uri("/v2/payment-requests/{id}/cancel", paymentLinkId)
                    .header("x-client-id", properties.getClientId())
                    .header("x-api-key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("cancellationReason", "Payment link expired"))
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            if (!root.has("code") || !"00".equals(root.get("code").asText())) {
                log.warn("PayOS cancel payment link response: {}", response);
            }
        } catch (Exception e) {
            log.warn("PayOS cancel payment link error for paymentLinkId={}", paymentLinkId, e);
        }
    }

    private String hmacSha256(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 computation failed", e);
        }
    }
}
