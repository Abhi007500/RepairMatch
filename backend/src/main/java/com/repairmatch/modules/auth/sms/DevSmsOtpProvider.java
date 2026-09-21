package com.repairmatch.modules.auth.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DevSmsOtpProvider implements SmsOtpProvider {

    private static final Logger logger = LoggerFactory.getLogger(DevSmsOtpProvider.class);

    @Override
    public SmsSendResult sendOtp(String phoneNumber, String otp) {
        String masked = maskPhone(phoneNumber);
        // Never log the raw OTP to production logs
        logger.debug("Simulated SMS OTP dispatch to {} via DevSmsOtpProvider", masked);

        return SmsSendResult.success(
                false,
                "Development mode: No real SMS dispatched. Use the test OTP or 123456.",
                getProviderName()
        );
    }

    @Override
    public String getProviderName() {
        return "DEV_SIMULATION";
    }

    @Override
    public boolean isConfigured() {
        return true;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 6) return "***";
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 3);
    }
}
