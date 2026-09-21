package com.repairmatch.modules.payment.service;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGatewayProvider {
    String createOrder(String receipt, BigDecimal amount, String currency, Map<String, String> notes);
    boolean verifySignature(String orderId, String paymentId, String signature);
    String getKeyId();
    boolean isConfigured();
}
