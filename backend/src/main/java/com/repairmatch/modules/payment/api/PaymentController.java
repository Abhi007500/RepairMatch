package com.repairmatch.modules.payment.api;

import com.repairmatch.modules.payment.dto.*;
import com.repairmatch.modules.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreatePaymentOrderRequest request) {
        PaymentOrderResponse response = paymentService.createPaymentOrder(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(
            Authentication authentication,
            @Valid @RequestBody VerifyPaymentRequest request) {
        PaymentResponse response = paymentService.verifyPayment(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/fail")
    public ResponseEntity<PaymentResponse> recordFailure(
            Authentication authentication,
            @Valid @RequestBody FailPaymentRequest request) {
        PaymentResponse response = paymentService.recordPaymentFailure(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentResponse>> getBookingPayments(
            Authentication authentication,
            @PathVariable String bookingId) {
        List<PaymentResponse> response = paymentService.getPaymentsForBooking(authentication.getName(), bookingId);
        return ResponseEntity.ok(response);
    }
}
