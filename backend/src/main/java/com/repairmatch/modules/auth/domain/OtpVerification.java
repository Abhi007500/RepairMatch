package com.repairmatch.modules.auth.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "otp_verifications")
@CompoundIndexes({
    @CompoundIndex(name = "otp_phone_verified_created_idx", def = "{'phoneNumber': 1, 'verified': 1, 'createdAt': -1}")
})
public class OtpVerification {

    @Id
    private String id;

    @Indexed
    private String phoneNumber;

    private String otpHash;
    private int attempts = 0;
    private int maxAttempts = 5;

    @Indexed
    private LocalDateTime expiresAt;

    private LocalDateTime resendAvailableAt;
    private boolean verified = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public OtpVerification() {}

    public OtpVerification(
            String id,
            String phoneNumber,
            String otpHash,
            int maxAttempts,
            LocalDateTime expiresAt,
            LocalDateTime resendAvailableAt) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.otpHash = otpHash;
        this.attempts = 0;
        this.maxAttempts = maxAttempts;
        this.expiresAt = expiresAt;
        this.resendAvailableAt = resendAvailableAt;
        this.verified = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getOtpHash() { return otpHash; }
    public void setOtpHash(String otpHash) { this.otpHash = otpHash; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getResendAvailableAt() { return resendAvailableAt; }
    public void setResendAvailableAt(LocalDateTime resendAvailableAt) { this.resendAvailableAt = resendAvailableAt; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
