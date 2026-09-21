package com.repairmatch.modules.auth.sms;

public interface SmsOtpProvider {
    SmsSendResult sendOtp(String phoneNumber, String otp);
    String getProviderName();
    boolean isConfigured();
}
