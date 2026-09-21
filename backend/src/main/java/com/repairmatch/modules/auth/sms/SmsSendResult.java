package com.repairmatch.modules.auth.sms;

public class SmsSendResult {
    private final boolean success;
    private final boolean dispatched;
    private final String message;
    private final String provider;

    public SmsSendResult(boolean success, boolean dispatched, String message, String provider) {
        this.success = success;
        this.dispatched = dispatched;
        this.message = message;
        this.provider = provider;
    }

    public static SmsSendResult success(boolean dispatched, String message, String provider) {
        return new SmsSendResult(true, dispatched, message, provider);
    }

    public static SmsSendResult failure(String message, String provider) {
        return new SmsSendResult(false, false, message, provider);
    }

    public boolean isSuccess() { return success; }
    public boolean isDispatched() { return dispatched; }
    public String getMessage() { return message; }
    public String getProvider() { return provider; }
}
