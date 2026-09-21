package com.repairmatch.modules.auth.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsProviderFactory {

    private final String configuredProvider;
    private final DevSmsOtpProvider devProvider;
    private final TwilioSmsOtpProvider twilioProvider;

    public SmsProviderFactory(
            @Value("${app.sms.provider:dev}") String configuredProvider,
            DevSmsOtpProvider devProvider,
            TwilioSmsOtpProvider twilioProvider) {
        this.configuredProvider = configuredProvider;
        this.devProvider = devProvider;
        this.twilioProvider = twilioProvider;
    }

    public SmsOtpProvider getProvider() {
        if ("twilio".equalsIgnoreCase(configuredProvider) && twilioProvider.isConfigured()) {
            return twilioProvider;
        }
        return devProvider;
    }
}
