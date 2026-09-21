package com.repairmatch.modules.auth.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Component
public class TwilioSmsOtpProvider implements SmsOtpProvider {

    private static final Logger logger = LoggerFactory.getLogger(TwilioSmsOtpProvider.class);

    private final String accountSid;
    private final String authToken;
    private final String fromNumber;
    private final RestClient restClient;

    public TwilioSmsOtpProvider(
            @Value("${app.sms.api-key:}") String accountSid,
            @Value("${app.sms.api-secret:}") String authToken,
            @Value("${app.sms.sender-id:}") String fromNumber) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
        this.restClient = RestClient.builder().build();
    }

    @Override
    public SmsSendResult sendOtp(String phoneNumber, String otp) {
        if (!isConfigured()) {
            return SmsSendResult.failure(
                    "SMS Provider (Twilio) is not configured. Please supply SMS_API_KEY and SMS_API_SECRET.",
                    getProviderName()
            );
        }

        try {
            String url = "https://api.twilio.com/2010-04-01/Accounts/" + accountSid + "/Messages.json";
            String credentials = Base64.getEncoder().encodeToString((accountSid + ":" + authToken).getBytes());
            String body = "Your RepairMatch verification code is: " + otp + ". Valid for 5 minutes. Do not share.";

            restClient.post()
                    .uri(url)
                    .header("Authorization", "Basic " + credentials)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body("To=" + phoneNumber + "&From=" + fromNumber + "&Body=" + body)
                    .retrieve()
                    .toBodilessEntity();

            return SmsSendResult.success(true, "SMS verification code dispatched via Twilio", getProviderName());
        } catch (Exception e) {
            logger.error("Failed to dispatch SMS via Twilio: {}", e.getMessage());
            return SmsSendResult.failure("Failed to deliver SMS: " + e.getMessage(), getProviderName());
        }
    }

    @Override
    public String getProviderName() {
        return "TWILIO";
    }

    @Override
    public boolean isConfigured() {
        return accountSid != null && !accountSid.isBlank() && authToken != null && !authToken.isBlank();
    }
}
