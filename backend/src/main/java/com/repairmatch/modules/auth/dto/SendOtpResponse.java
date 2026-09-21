package com.repairmatch.modules.auth.dto;

public class SendOtpResponse {
    private boolean success;
    private String message;
    private String phoneNumber;
    private int cooldownSeconds;
    private int expiresInSeconds;
    private boolean smsDispatched;
    private String demoOtp; // Available strictly when local demo fallback is enabled

    public SendOtpResponse() {}

    public SendOtpResponse(
            boolean success,
            String message,
            String phoneNumber,
            int cooldownSeconds,
            int expiresInSeconds,
            boolean smsDispatched,
            String demoOtp) {
        this.success = success;
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.cooldownSeconds = cooldownSeconds;
        this.expiresInSeconds = expiresInSeconds;
        this.smsDispatched = smsDispatched;
        this.demoOtp = demoOtp;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public int getCooldownSeconds() { return cooldownSeconds; }
    public void setCooldownSeconds(int cooldownSeconds) { this.cooldownSeconds = cooldownSeconds; }

    public int getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(int expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }

    public boolean isSmsDispatched() { return smsDispatched; }
    public void setSmsDispatched(boolean smsDispatched) { this.smsDispatched = smsDispatched; }

    public String getDemoOtp() { return demoOtp; }
    public void setDemoOtp(String demoOtp) { this.demoOtp = demoOtp; }

    // Backward compatibility for previewOtp getter/setter
    public String getPreviewOtp() { return demoOtp; }
    public void setPreviewOtp(String previewOtp) { this.demoOtp = previewOtp; }
}
