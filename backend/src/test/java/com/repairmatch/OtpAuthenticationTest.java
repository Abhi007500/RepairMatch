package com.repairmatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairmatch.modules.auth.domain.OtpVerification;
import com.repairmatch.modules.auth.dto.SendOtpRequest;
import com.repairmatch.modules.auth.dto.VerifyOtpRequest;
import com.repairmatch.modules.auth.repository.OtpVerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OtpAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OtpVerificationRepository otpVerificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        otpVerificationRepository.deleteAll();
    }

    @Test
    void testRequestOtpSuccess() throws Exception {
        SendOtpRequest req = new SendOtpRequest("9876543210");

        MvcResult result = mockMvc.perform(post("/api/auth/otp/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.phoneNumber").value("9876543210"))
                .andExpect(jsonPath("$.cooldownSeconds").value(60))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(json.has("message"));
        assertTrue(otpVerificationRepository.findLatestActive("9876543210").isPresent());
    }

    @Test
    void testVerifyOtpSuccess() throws Exception {
        String phone = "9876543210";
        String rawOtp = "654321";

        // Seed hashed OTP in database
        OtpVerification verification = new OtpVerification(
                UUID.randomUUID().toString(),
                phone,
                passwordEncoder.encode(rawOtp),
                5,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().minusSeconds(10)
        );
        otpVerificationRepository.save(verification);

        VerifyOtpRequest req = new VerifyOtpRequest(phone, rawOtp);

        mockMvc.perform(post("/api/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.email").value("rahul@gmail.com"));

        // Verify that OTP record is marked as verified
        OtpVerification updated = otpVerificationRepository.findById(verification.getId()).orElseThrow();
        assertTrue(updated.isVerified());
    }

    @Test
    void testIncorrectOtpIncrementsAttempts() throws Exception {
        String phone = "9876543210";
        String rawOtp = "888999";

        OtpVerification verification = new OtpVerification(
                UUID.randomUUID().toString(),
                phone,
                passwordEncoder.encode(rawOtp),
                5,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().minusSeconds(10)
        );
        otpVerificationRepository.save(verification);

        VerifyOtpRequest req = new VerifyOtpRequest(phone, "000000");

        mockMvc.perform(post("/api/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid verification code. 4 attempts remaining."));

        OtpVerification updated = otpVerificationRepository.findById(verification.getId()).orElseThrow();
        assertEquals(1, updated.getAttempts());
    }

    @Test
    void testMaxAttemptsExceededBlocks() throws Exception {
        String phone = "9876543210";
        String rawOtp = "777666";

        OtpVerification verification = new OtpVerification(
                UUID.randomUUID().toString(),
                phone,
                passwordEncoder.encode(rawOtp),
                5,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().minusSeconds(10)
        );
        verification.setAttempts(5);
        otpVerificationRepository.save(verification);

        VerifyOtpRequest req = new VerifyOtpRequest(phone, rawOtp);

        mockMvc.perform(post("/api/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Maximum verification attempts exceeded. Please request a new code."));
    }

    @Test
    void testExpiredOtpRejected() throws Exception {
        String phone = "9876543210";
        String rawOtp = "112233";

        // Expired in past
        OtpVerification verification = new OtpVerification(
                UUID.randomUUID().toString(),
                phone,
                passwordEncoder.encode(rawOtp),
                5,
                LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().minusMinutes(3)
        );
        otpVerificationRepository.save(verification);

        VerifyOtpRequest req = new VerifyOtpRequest(phone, rawOtp);

        mockMvc.perform(post("/api/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Verification code has expired. Please request a new code."));
    }

    @Test
    void testResendCooldownEnforced() throws Exception {
        SendOtpRequest req = new SendOtpRequest("9876543210");

        // First send
        mockMvc.perform(post("/api/auth/otp/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        // Immediate second send should be blocked by cooldown
        mockMvc.perform(post("/api/auth/otp/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("seconds before requesting another code.")));
    }

    @Test
    void testOtpInvalidatedAfterVerification() throws Exception {
        String phone = "9876543210";
        String rawOtp = "998877";

        OtpVerification verification = new OtpVerification(
                UUID.randomUUID().toString(),
                phone,
                passwordEncoder.encode(rawOtp),
                5,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().minusSeconds(10)
        );
        otpVerificationRepository.save(verification);

        VerifyOtpRequest req = new VerifyOtpRequest(phone, rawOtp);

        // First verification succeeds
        mockMvc.perform(post("/api/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        // Second verification with same code fails
        mockMvc.perform(post("/api/auth/otp/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No active verification code found for this number. Please request a new code."));
    }

    @Test
    void testInvalidIndianMobileNumberRejected() throws Exception {
        // Invalid number (e.g. 5 digits or starting with 1)
        SendOtpRequest req = new SendOtpRequest("12345");

        mockMvc.perform(post("/api/auth/otp/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("valid 10-digit Indian mobile number")));
    }
}
