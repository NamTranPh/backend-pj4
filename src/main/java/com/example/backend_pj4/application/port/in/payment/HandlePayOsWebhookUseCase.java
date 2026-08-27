package com.example.backend_pj4.application.port.in.payment;

public interface HandlePayOsWebhookUseCase {
    void execute(String webhookBody);
}
