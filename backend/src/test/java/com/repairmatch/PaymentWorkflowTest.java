package com.repairmatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairmatch.common.security.JwtTokenProvider;
import com.repairmatch.modules.booking.domain.Booking;
import com.repairmatch.modules.booking.repository.BookingRepository;
import com.repairmatch.modules.booking.repository.BookingTimelineRepository;
import com.repairmatch.modules.payment.domain.Payment;
import com.repairmatch.modules.payment.dto.CreatePaymentOrderRequest;
import com.repairmatch.modules.payment.dto.FailPaymentRequest;
import com.repairmatch.modules.payment.dto.VerifyPaymentRequest;
import com.repairmatch.modules.payment.repository.PaymentRepository;
import com.repairmatch.modules.payment.service.RazorpayPaymentGatewayProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentWorkflowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingTimelineRepository timelineRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RazorpayPaymentGatewayProvider paymentGatewayProvider;

    private String getCustomerToken() {
        return jwtTokenProvider.generateTokenFromEmail("rahul@gmail.com", "usr-cust-1", "CUSTOMER", "Rahul Verma");
    }

    private String getOtherCustomerToken() {
        return jwtTokenProvider.generateTokenFromEmail("priya@gmail.com", "usr-cust-2", "CUSTOMER", "Priya Sharma");
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        bookingRepository.findAll().stream()
                .filter(b -> "usr-cust-1".equals(b.getCustomer().getId()))
                .forEach(b -> {
                    b.setPaymentStatus("PENDING");
                    bookingRepository.save(b);
                });
    }

    @Test
    void testCreatePaymentOrderSuccess() throws Exception {
        // Find existing booking for Rahul (usr-cust-1)
        Booking booking = bookingRepository.findAll().stream()
                .filter(b -> "usr-cust-1".equals(b.getCustomer().getId()) && !"PAID".equals(b.getPaymentStatus()))
                .findFirst()
                .orElseThrow();

        CreatePaymentOrderRequest req = new CreatePaymentOrderRequest(booking.getId());

        MvcResult result = mockMvc.perform(post("/api/payments/create-order")
                        .header("Authorization", "Bearer " + getCustomerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").isNotEmpty())
                .andExpect(jsonPath("$.providerOrderId").isNotEmpty())
                .andExpect(jsonPath("$.amount").isNotEmpty())
                .andExpect(jsonPath("$.provider").value("RAZORPAY"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        String paymentId = json.get("paymentId").asText();

        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        assertEquals("PENDING", payment.getPaymentStatus());
        assertEquals("RAZORPAY", payment.getProvider());
    }

    @Test
    void testValidHmacSignatureVerification() throws Exception {
        Booking booking = bookingRepository.findAll().stream()
                .filter(b -> "usr-cust-1".equals(b.getCustomer().getId()) && !"PAID".equals(b.getPaymentStatus()))
                .findFirst()
                .orElseThrow();

        // 1. Create order
        CreatePaymentOrderRequest orderReq = new CreatePaymentOrderRequest(booking.getId());
        MvcResult orderResult = mockMvc.perform(post("/api/payments/create-order")
                        .header("Authorization", "Bearer " + getCustomerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode orderJson = objectMapper.readTree(orderResult.getResponse().getContentAsString());
        String paymentId = orderJson.get("paymentId").asText();
        String orderId = orderJson.get("providerOrderId").asText();
        String payId = "pay_test_" + System.currentTimeMillis();

        // Cryptographically compute HMAC signature using the test secret
        String validSignature = paymentGatewayProvider.generateSignature(orderId, payId);

        // 2. Verify payment
        VerifyPaymentRequest verifyReq = new VerifyPaymentRequest(paymentId, orderId, payId, validSignature);

        mockMvc.perform(post("/api/payments/verify")
                        .header("Authorization", "Bearer " + getCustomerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PAID"))
                .andExpect(jsonPath("$.providerPaymentId").value(payId));

        // 3. Verify Database state
        Payment updatedPayment = paymentRepository.findById(paymentId).orElseThrow();
        assertEquals("PAID", updatedPayment.getPaymentStatus());

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals("PAID", updatedBooking.getPaymentStatus());

        // 4. Verify Timeline entry
        boolean hasPaidTimeline = timelineRepository.findByBookingIdOrderByCreatedAtAsc(booking.getId()).stream()
                .anyMatch(t -> "PAID".equals(t.getStatus()));
        assertTrue(hasPaidTimeline);
    }

    @Test
    void testInvalidHmacSignatureRejected() throws Exception {
        Booking booking = bookingRepository.findAll().stream()
                .filter(b -> "usr-cust-1".equals(b.getCustomer().getId()))
                .findFirst()
                .orElseThrow();

        CreatePaymentOrderRequest orderReq = new CreatePaymentOrderRequest(booking.getId());
        MvcResult orderResult = mockMvc.perform(post("/api/payments/create-order")
                        .header("Authorization", "Bearer " + getCustomerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode orderJson = objectMapper.readTree(orderResult.getResponse().getContentAsString());
        String paymentId = orderJson.get("paymentId").asText();
        String orderId = orderJson.get("providerOrderId").asText();

        // Invalid signature
        VerifyPaymentRequest verifyReq = new VerifyPaymentRequest(paymentId, orderId, "pay_bogus", "bad_signature_hash");

        mockMvc.perform(post("/api/payments/verify")
                        .header("Authorization", "Bearer " + getCustomerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid payment signature")));

        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        assertEquals("FAILED", payment.getPaymentStatus());
    }

    @Test
    void testIdempotentDuplicatePaymentCallback() throws Exception {
        Booking booking = bookingRepository.findAll().stream()
                .filter(b -> "usr-cust-1".equals(b.getCustomer().getId()))
                .findFirst()
                .orElseThrow();

        CreatePaymentOrderRequest orderReq = new CreatePaymentOrderRequest(booking.getId());
        MvcResult orderResult = mockMvc.perform(post("/api/payments/create-order")
                        .header("Authorization", "Bearer " + getCustomerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode orderJson = objectMapper.readTree(orderResult.getResponse().getContentAsString());
        String paymentId = orderJson.get("paymentId").asText();
        String orderId = orderJson.get("providerOrderId").asText();
        String payId = "pay_idempotent_" + System.currentTimeMillis();
        String signature = paymentGatewayProvider.generateSignature(orderId, payId);

        VerifyPaymentRequest verifyReq = new VerifyPaymentRequest(paymentId, orderId, payId, signature);

        // First verification
        mockMvc.perform(post("/api/payments/verify")
                        .header("Authorization", "Bearer " + getCustomerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));

        // Second verification should be idempotent and return 200 OK without errors
        mockMvc.perform(post("/api/payments/verify")
                        .header("Authorization", "Bearer " + getCustomerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }

    @Test
    void testPaymentFailureHandling() throws Exception {
        Booking booking = bookingRepository.findAll().stream()
                .filter(b -> "usr-cust-1".equals(b.getCustomer().getId()))
                .findFirst()
                .orElseThrow();

        CreatePaymentOrderRequest orderReq = new CreatePaymentOrderRequest(booking.getId());
        MvcResult orderResult = mockMvc.perform(post("/api/payments/create-order")
                        .header("Authorization", "Bearer " + getCustomerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode orderJson = objectMapper.readTree(orderResult.getResponse().getContentAsString());
        String paymentId = orderJson.get("paymentId").asText();

        FailPaymentRequest failReq = new FailPaymentRequest(paymentId, "User cancelled on checkout screen", "BAD_REQUEST_ERROR");

        mockMvc.perform(post("/api/payments/fail")
                        .header("Authorization", "Bearer " + getCustomerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("FAILED"))
                .andExpect(jsonPath("$.failureReason").value("User cancelled on checkout screen"));
    }

    @Test
    void testUnauthorizedPaymentAccessRejected() throws Exception {
        // Booking belongs to Rahul (usr-cust-1)
        Booking booking = bookingRepository.findAll().stream()
                .filter(b -> "usr-cust-1".equals(b.getCustomer().getId()))
                .findFirst()
                .orElseThrow();

        CreatePaymentOrderRequest orderReq = new CreatePaymentOrderRequest(booking.getId());

        // Priya (usr-cust-2) tries to initiate payment on Rahul's booking
        mockMvc.perform(post("/api/payments/create-order")
                        .header("Authorization", "Bearer " + getOtherCustomerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testBookingPaymentConsistency() throws Exception {
        Booking booking = bookingRepository.findAll().stream()
                .filter(b -> "usr-cust-1".equals(b.getCustomer().getId()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(get("/api/payments/booking/" + booking.getId())
                        .header("Authorization", "Bearer " + getCustomerToken()))
                .andExpect(status().isOk());
    }
}
